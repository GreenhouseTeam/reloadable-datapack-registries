package dev.greenhouseteam.rdpr.mixin;

import dev.greenhouseteam.rdpr.impl.ReloadableDatapackRegistries;
import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @Inject(method = "load", at = @At("HEAD"))
    private static <D, R> void rdpr$initReloadableDatapackRegistries(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<D> worldDataSupplier, WorldLoader.ResultFactory<D, R> resultFactory, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<R>> cir) {
        ReloadableDatapackRegistries.init();
    }
}
