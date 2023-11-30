package dev.greenhouseteam.reloadabledatapackregistries.mixin;

import com.google.common.collect.ImmutableList;
import dev.greenhouseteam.reloadabledatapackregistries.impl.ReloadableDatapackRegistries;
import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadRegistriesClientboundPacket;
import dev.greenhouseteam.reloadabledatapackregistries.platform.services.IRDRPlatformHelper;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private RegistryAccess.Frozen reloadabledatapackregistries$previousFrozenAccess;

    @Shadow public abstract LayeredRegistryAccess<RegistryLayer> registries();

    @Final @Shadow @Mutable
    private LayeredRegistryAccess<RegistryLayer> registries;

    @Shadow public abstract ResourceManager getResourceManager();

    @Shadow public abstract PlayerList getPlayerList();

    @Inject(method = "method_29437", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/MultiPackResourceManager;<init>(Lnet/minecraft/server/packs/PackType;Ljava/util/List;)V", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void reloadabledatapackregistries$reloadReloadableRegistries(RegistryAccess.Frozen unusedFrozen, ImmutableList immutableList, CallbackInfoReturnable<CompletionStage> cir, CloseableResourceManager resourceManager) {
        if (!ReloadableDatapackRegistries.getOrCreateAllRegistryData().isEmpty()) {
            RegistryAccess.Frozen previous = this.registries().getLayer(RegistryLayer.RELOADABLE);
            this.reloadabledatapackregistries$previousFrozenAccess = previous;
            try {
                RegistryAccess.Frozen frozen = new RegistryAccess.ImmutableRegistryAccess(previous.registries().filter(registryEntry -> !ReloadableDatapackRegistries.isReloadableRegistry(registryEntry.key()))).freeze();
                RegistryAccess.Frozen loadedFrozen = RegistryDataLoader.load(this.getResourceManager(), frozen, ReloadableDatapackRegistries.getOrCreateAllRegistryData());
                this.registries = this.registries().replaceFrom(RegistryLayer.RELOADABLE, loadedFrozen);
                if (ReloadableDatapackRegistries.hasNetworkableRegistries()) {
                    IRDRPlatformHelper.INSTANCE.sendReloadPacket(new ReloadRegistriesClientboundPacket(new RegistryAccess.ImmutableRegistryAccess(loadedFrozen.registries().filter(registryEntry -> ReloadableDatapackRegistries.isNetworkable(registryEntry.key()))).freeze()), this.getPlayerList().getPlayers());
                }
            } catch (Exception ex) {
                reloadabledatapackregistries$previousFrozenAccess = null;
                cir.setReturnValue(CompletableFuture.failedFuture(ex));
            }
        }
    }

    @ModifyArg(method = "method_29437", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess$Frozen;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"), index = 1)
    private RegistryAccess.Frozen reloadabledatapackregistries$reloadReloadableRegistries(RegistryAccess.Frozen original) {
        if (reloadabledatapackregistries$previousFrozenAccess == null)
            return original;

        return this.registries().getAccessFrom(RegistryLayer.STATIC);
    }

    @ModifyVariable(method = "reloadResources", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/core/LayeredRegistryAccess;getAccessForLoading(Ljava/lang/Object;)Lnet/minecraft/core/RegistryAccess$Frozen;"))
    private RegistryAccess.Frozen reloadabledatapackregistries$freezeWithReloadablesInMind(RegistryAccess.Frozen original) {
        if (reloadabledatapackregistries$previousFrozenAccess == null)
            return original;

        return this.registries().getAccessFrom(RegistryLayer.STATIC);
    }

    @Inject(method = "reloadResources", at = @At("RETURN"))
    private void reloadabledatapackregistries$keepOldDataUponFail(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (reloadabledatapackregistries$previousFrozenAccess != null && cir.getReturnValue().isCompletedExceptionally()) {
            this.registries().replaceFrom(RegistryLayer.RELOADABLE, reloadabledatapackregistries$previousFrozenAccess);
        }
        reloadabledatapackregistries$previousFrozenAccess = null;
    }
}
