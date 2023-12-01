package dev.greenhouseteam.rdpr.mixin;

import com.google.common.collect.ImmutableList;
import dev.greenhouseteam.rdpr.impl.ReloadableDatapackRegistries;
import dev.greenhouseteam.rdpr.impl.util.LayeredRegistryAccessUtil;
import dev.greenhouseteam.rdpr.network.ReloadRegistriesClientboundPacket;
import dev.greenhouseteam.rdpr.platform.IRDPRPlatformHelper;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private RegistryAccess.Frozen rdpr$previousFrozenAccess;

    @Shadow public abstract LayeredRegistryAccess<RegistryLayer> registries();

    @Shadow public abstract PlayerList getPlayerList();

    @Inject(method = "lambda$reloadResources$26", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/MultiPackResourceManager;<init>(Lnet/minecraft/server/packs/PackType;Ljava/util/List;)V", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void rdpr$reloadReloadableRegistries(RegistryAccess.Frozen unusedFrozen, ImmutableList immutableList, CallbackInfoReturnable<CompletionStage> cir, CloseableResourceManager resourceManager) {
        if (!ReloadableDatapackRegistries.getAllRegistryData().isEmpty()) {
            RegistryAccess.Frozen previous = this.registries().getLayer(RegistryLayer.WORLDGEN);
            this.rdpr$previousFrozenAccess = previous;
            try {
                RegistryAccess.Frozen frozen = new RegistryAccess.ImmutableRegistryAccess(previous.registries().filter(registryEntry -> !ReloadableDatapackRegistries.isReloadableRegistry(registryEntry.key()))).freeze();
                RegistryAccess.Frozen loadedFrozen = RegistryDataLoader.load(resourceManager, frozen, ReloadableDatapackRegistries.getAllRegistryData());
                LayeredRegistryAccessUtil.replaceSpecificLayer(this.registries(), RegistryLayer.WORLDGEN, new RegistryAccess.ImmutableRegistryAccess(Stream.concat(previous.registries().filter(registryEntry -> !ReloadableDatapackRegistries.isReloadableRegistry(registryEntry.key())), loadedFrozen.registries())).freeze());
                if (ReloadableDatapackRegistries.hasNetworkableRegistries())
                    IRDPRPlatformHelper.INSTANCE.sendReloadPacket(new ReloadRegistriesClientboundPacket(loadedFrozen), this.getPlayerList().getPlayers());
                rdpr$previousFrozenAccess = null;
            } catch (Exception ex) {
                rdpr$previousFrozenAccess = null;
                cir.setReturnValue(CompletableFuture.failedFuture(ex));
            }
        }
    }

    @ModifyArg(method = "lambda$reloadResources$26", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess$Frozen;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"), index = 1)
    private RegistryAccess.Frozen rdpr$reloadListenersWithNewFrozen(RegistryAccess.Frozen original) {
        if (ReloadableDatapackRegistries.getAllRegistryData().isEmpty())
            return original;

        return this.registries().getAccessForLoading(RegistryLayer.RELOADABLE);
    }

    @Inject(method = "reloadResources", at = @At("RETURN"))
    private void rdpr$keepOldDataUponFail(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (rdpr$previousFrozenAccess != null && cir.getReturnValue().isCompletedExceptionally()) {
            LayeredRegistryAccessUtil.replaceSpecificLayer(this.registries(), RegistryLayer.WORLDGEN, rdpr$previousFrozenAccess);
        }
        CompletableFuture.supplyAsync(() -> rdpr$previousFrozenAccess = null);
    }
}
