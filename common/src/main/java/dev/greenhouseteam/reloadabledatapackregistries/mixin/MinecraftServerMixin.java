package dev.greenhouseteam.reloadabledatapackregistries.mixin;

import dev.greenhouseteam.reloadabledatapackregistries.ReloadableDatapackRegistries;
import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadRegistriesClientboundPacket;
import dev.greenhouseteam.reloadabledatapackregistries.platform.services.IRDRPlatformHelper;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private RegistryAccess.Frozen reloadabledatapackregistries$previousFrozenAccess;
    @Shadow public abstract LayeredRegistryAccess<RegistryLayer> registries();

    @Shadow public abstract ResourceManager getResourceManager();

    @Shadow public abstract PlayerList getPlayerList();

    @Inject(method = "reloadResources", at = @At("HEAD"), cancellable = true)
    private void reloadabledatapackregistries$reloadReloadableRegistries(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (!ReloadableDatapackRegistries.getOrCreateAllRegistryData().isEmpty()) {
            RegistryAccess.Frozen previous = this.registries().getLayer(RegistryLayer.RELOADABLE);
            this.reloadabledatapackregistries$previousFrozenAccess = previous;
            try {
                RegistryAccess.Frozen frozen = new RegistryAccess.ImmutableRegistryAccess(previous.registries().filter(registryEntry -> !ReloadableDatapackRegistries.isReloadableRegistry(registryEntry.key()))).freeze();
                RegistryAccess.Frozen loadedFrozen = RegistryDataLoader.load(this.getResourceManager(), frozen, ReloadableDatapackRegistries.getOrCreateAllRegistryData());
                this.registries().replaceFrom(RegistryLayer.RELOADABLE, loadedFrozen);
                if (ReloadableDatapackRegistries.hasNetworkableRegistries()) {
                    IRDRPlatformHelper.INSTANCE.sendReloadPacket(new ReloadRegistriesClientboundPacket(new RegistryAccess.ImmutableRegistryAccess(loadedFrozen.registries().filter(registryEntry -> ReloadableDatapackRegistries.isNetworkable(registryEntry.key()))).freeze()), this.getPlayerList().getPlayers());
                }
            } catch (Exception ex) {
                reloadabledatapackregistries$previousFrozenAccess = null;
                cir.setReturnValue(CompletableFuture.failedFuture(ex));
            }
        }
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
