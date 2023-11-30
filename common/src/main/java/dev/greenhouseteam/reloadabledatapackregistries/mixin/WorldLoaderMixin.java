package dev.greenhouseteam.reloadabledatapackregistries.mixin;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.reloadabledatapackregistries.ReloadableDatapackRegistries;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {

    @Unique
    private static CloseableResourceManager reloadabledatapackregistries$capturedCloseableResourceManager;
    @Unique
    private static LayeredRegistryAccess<RegistryLayer> reloadabledatapackregistries$capturedLayeredRegistryAccess;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/LayeredRegistryAccess;replaceFrom(Ljava/lang/Object;[Lnet/minecraft/core/RegistryAccess$Frozen;)Lnet/minecraft/core/LayeredRegistryAccess;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <D, R> void reloadabledatapackregistries$captureCloseableResourceManager(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<D> worldDataSupplier, WorldLoader.ResultFactory<D, R> resultFactory, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<R>> cir, Pair<WorldDataConfiguration, CloseableResourceManager> pair, CloseableResourceManager closeableResourceManager) {
        reloadabledatapackregistries$capturedCloseableResourceManager = closeableResourceManager;
    }

    @ModifyVariable(method = "load", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/core/LayeredRegistryAccess;replaceFrom(Ljava/lang/Object;[Lnet/minecraft/core/RegistryAccess$Frozen;)Lnet/minecraft/core/LayeredRegistryAccess;"), ordinal = 2)
    private static LayeredRegistryAccess<RegistryLayer> reloadabledatapackregistries$loadReloadableRegistries(LayeredRegistryAccess<RegistryLayer> original) {
        if (ReloadableDatapackRegistries.getOrCreateAllRegistryData().isEmpty())
            return original;

        RegistryAccess.Frozen frozen = new RegistryAccess.ImmutableRegistryAccess(original.getLayer(RegistryLayer.RELOADABLE).registries().filter(registryEntry -> !ReloadableDatapackRegistries.isReloadableRegistry(registryEntry.key()))).freeze();
        RegistryAccess.Frozen loadedFrozen = RegistryDataLoader.load(reloadabledatapackregistries$capturedCloseableResourceManager, frozen, ReloadableDatapackRegistries.getOrCreateAllRegistryData());
        reloadabledatapackregistries$capturedLayeredRegistryAccess = original.replaceFrom(RegistryLayer.RELOADABLE, loadedFrozen);
        return reloadabledatapackregistries$capturedLayeredRegistryAccess;
    }

    @ModifyVariable(method = "load", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/core/LayeredRegistryAccess;getAccessForLoading(Ljava/lang/Object;)Lnet/minecraft/core/RegistryAccess$Frozen;", ordinal = 1), ordinal = 2)
    private static RegistryAccess.Frozen reloadabledatapackregistries$freezeWithReloadableRegistries(RegistryAccess.Frozen original) {
        if (reloadabledatapackregistries$capturedLayeredRegistryAccess == null)
            return original;

        RegistryAccess.Frozen frozen = reloadabledatapackregistries$capturedLayeredRegistryAccess.getAccessFrom(RegistryLayer.STATIC);
        reloadabledatapackregistries$capturedLayeredRegistryAccess = null;
        return frozen;
    }

}
