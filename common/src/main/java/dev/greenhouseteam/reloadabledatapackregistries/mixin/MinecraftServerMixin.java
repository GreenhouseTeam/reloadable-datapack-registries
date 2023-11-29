package dev.greenhouseteam.reloadabledatapackregistries.mixin;

import dev.greenhouseteam.reloadabledatapackregistries.ReloadableDatapackRegistries;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private RegistryAccess.Frozen reloadabledatapackregistries$previousFrozenAccess;
    @Shadow public abstract LayeredRegistryAccess<RegistryLayer> registries();

    @Shadow public abstract ResourceManager getResourceManager();

    @Inject(method = "reloadResources", at = @At("HEAD"), cancellable = true)
    private void reloadabledatapackregistries$reloadReloadableRegistries(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (!ReloadableDatapackRegistries.getAllReloadableRegistryData().isEmpty()) {
            RegistryAccess.Frozen previous = this.registries().getLayer(RegistryLayer.RELOADABLE);
            this.reloadabledatapackregistries$previousFrozenAccess = previous;
            try {
                RegistryAccess.Frozen frozen = new RegistryAccess.ImmutableRegistryAccess(previous.registries().filter(registryEntry -> !ReloadableDatapackRegistries.isReloadableRegistry(registryEntry.key()))).freeze();
                RegistryAccess.Frozen loadedFrozen = RegistryDataLoader.load(getResourceManager(), frozen, ReloadableDatapackRegistries.getAllReloadableRegistryData());
                this.registries().replaceFrom(RegistryLayer.RELOADABLE, loadedFrozen);
                this.reloadabledatapackregistries$previousFrozenAccess = null;
            } catch (Exception ex) {
                cir.setReturnValue(CompletableFuture.failedFuture(ex));
            }
        }
    }

    @Inject(method = "reloadResources", at = @At("RETURN"))
    private void reloadabledatapackregistries$keepOldDataUponFail(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (reloadabledatapackregistries$previousFrozenAccess != null && cir.getReturnValue().isCompletedExceptionally()) {
            this.registries().replaceFrom(RegistryLayer.RELOADABLE, reloadabledatapackregistries$previousFrozenAccess);
            reloadabledatapackregistries$previousFrozenAccess = null;
        }
    }
}
