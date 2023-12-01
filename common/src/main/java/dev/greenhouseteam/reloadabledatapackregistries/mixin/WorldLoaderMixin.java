package dev.greenhouseteam.reloadabledatapackregistries.mixin;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.reloadabledatapackregistries.impl.ReloadableDatapackRegistries;
import dev.greenhouseteam.reloadabledatapackregistries.impl.util.LayeredRegistryAccessUtil;
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
import java.util.stream.Stream;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {

    @Unique
    private static CloseableResourceManager reloadabledatapackregistries$capturedCloseableResourceManager;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/WorldLoader;loadAndReplaceLayer(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/server/RegistryLayer;Ljava/util/List;)Lnet/minecraft/core/LayeredRegistryAccess;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <D, R> void reloadabledatapackregistries$captureCloseableResourceManager(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<D> worldDataSupplier, WorldLoader.ResultFactory<D, R> resultFactory, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<R>> cir, Pair<WorldDataConfiguration, CloseableResourceManager> pair, CloseableResourceManager closeableResourceManager) {
        reloadabledatapackregistries$capturedCloseableResourceManager = closeableResourceManager;
    }

    @ModifyVariable(method = "load", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/WorldLoader;loadAndReplaceLayer(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/server/RegistryLayer;Ljava/util/List;)Lnet/minecraft/core/LayeredRegistryAccess;"), ordinal = 1)
    private static LayeredRegistryAccess<RegistryLayer> reloadabledatapackregistries$loadReloadableRegistries(LayeredRegistryAccess<RegistryLayer> original) {
        if (ReloadableDatapackRegistries.getAllRegistryData().isEmpty())
            return original;

        RegistryAccess.Frozen frozen = new RegistryAccess.ImmutableRegistryAccess(original.getLayer(RegistryLayer.WORLDGEN).registries().filter(registryEntry -> !ReloadableDatapackRegistries.isReloadableRegistry(registryEntry.key()))).freeze();
        RegistryAccess.Frozen loadedFrozen = RegistryDataLoader.load(reloadabledatapackregistries$capturedCloseableResourceManager, frozen, ReloadableDatapackRegistries.getAllRegistryData());
        LayeredRegistryAccessUtil.replaceSpecificLayer(original, RegistryLayer.WORLDGEN, new RegistryAccess.ImmutableRegistryAccess(Stream.concat(frozen.registries(), loadedFrozen.registries())).freeze());
        return original;
    }
}
