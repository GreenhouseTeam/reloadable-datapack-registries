package dev.greenhouseteam.reloadabledatapackregistries.mixin.neoforge;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.reloadabledatapackregistries.ReloadableDatapackRegistries;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {

    @Unique
    private static LayeredRegistryAccess<RegistryLayer> reloadabledatapackregistries$capturedLayeredRegistryAccess;
    @Unique
    private static RegistryAccess.Frozen reloadabledatapackregistries$capturedFrozenLocals;

    @Shadow
    private static LayeredRegistryAccess<RegistryLayer> loadAndReplaceLayer(ResourceManager resourceManager, LayeredRegistryAccess<RegistryLayer> layeredAccess, RegistryLayer registryLayer, List<RegistryDataLoader.RegistryData<?>> p_250589_) { throw new RuntimeException(""); }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess$Frozen;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <D, R> void reloadabledatapackregistries$loadReloadableRegistries(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<D> worldDataSupplier, WorldLoader.ResultFactory<D, R> worldLoaderResultFactory, Executor worldCreationExecutor, Executor backgroundExecutor, CallbackInfoReturnable<CompletableFuture<R>> cir,
                                                                              Pair<WorldDataConfiguration, CloseableResourceManager> createdResourceManager, CloseableResourceManager closeableResourceManager, LayeredRegistryAccess<RegistryLayer> newRegistryAccess, LayeredRegistryAccess<RegistryLayer> loadedWorldgenRegistries,
                                                                              RegistryAccess.Frozen frozenDimensioneStage, RegistryAccess.Frozen loadedDimensionRegistries, WorldDataConfiguration worldDataConfiguration, WorldLoader.DataLoadOutput<D> dataLoadOutput,
                                                                              LayeredRegistryAccess<RegistryLayer> finalDimensions, RegistryAccess.Frozen frozenReloadableStage) {
        if (ReloadableDatapackRegistries.getAllReloadableRegistryData().isEmpty()) return;
        LayeredRegistryAccess<RegistryLayer> reloadableLayeredRegistry = loadAndReplaceLayer(
                closeableResourceManager, finalDimensions, RegistryLayer.RELOADABLE, ReloadableDatapackRegistries.getAllReloadableRegistryData()
        );
        if (reloadableLayeredRegistry.getLayer(RegistryLayer.RELOADABLE).registries().findAny().isPresent()) {
            reloadabledatapackregistries$capturedLayeredRegistryAccess = reloadableLayeredRegistry;
            reloadabledatapackregistries$capturedFrozenLocals = reloadableLayeredRegistry.getAccessForLoading(RegistryLayer.RELOADABLE);
        }
    }

    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess$Frozen;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"), index = 1)
    private static RegistryAccess.Frozen reloadabledatapackregistries$loadWithReloadablesInMind(RegistryAccess.Frozen original) {
        if (reloadabledatapackregistries$capturedFrozenLocals != null) {
            return reloadabledatapackregistries$capturedFrozenLocals;
        }
        return original;
    }

    @ModifyArg(method = "lambda$load$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V"))
    private static RegistryAccess reloadabledatapackregistries$updateTagsWithReloadableRegistries(RegistryAccess original) {
        if (reloadabledatapackregistries$capturedFrozenLocals != null) {
            return reloadabledatapackregistries$capturedFrozenLocals;
        }
        return original;
    }

    @ModifyArg(method = "lambda$load$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/WorldLoader$ResultFactory;create(Lnet/minecraft/server/packs/resources/CloseableResourceManager;Lnet/minecraft/server/ReloadableServerResources;Lnet/minecraft/core/LayeredRegistryAccess;Ljava/lang/Object;)Ljava/lang/Object;"), index = 2)
    private static LayeredRegistryAccess<RegistryLayer> reloadabledatapackregistries$createWithReloadablesInMind(LayeredRegistryAccess<RegistryLayer> original) {
        if (reloadabledatapackregistries$capturedLayeredRegistryAccess != null) {
            return reloadabledatapackregistries$capturedLayeredRegistryAccess;
        }
        return original;
    }

    @Inject(method = "load", at = @At("TAIL"))
    private static <D, R> void reloadabledatapackregistries$createWithReloadablesInMind(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<D> worldDataSupplier, WorldLoader.ResultFactory<D, R> worldLoaderResultFactory, Executor worldCreationExecutor, Executor backgroundExecutor, CallbackInfoReturnable<CompletableFuture<R>> cir) {
        reloadabledatapackregistries$capturedLayeredRegistryAccess = null;
        reloadabledatapackregistries$capturedFrozenLocals = null;
    }

}
