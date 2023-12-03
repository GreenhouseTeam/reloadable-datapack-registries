package dev.greenhouseteam.rdpr.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import dev.greenhouseteam.rdpr.impl.ReloadableDatapackRegistries;
import dev.greenhouseteam.rdpr.impl.ReloadableRegistryData;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RegistryDataLoader.RegistryData.class)
public abstract class RegistryDataLoaderRegistryDataMixin<T> {

    @Shadow public abstract ResourceKey<? extends Registry<T>> key();

    @Unique
    private Map<ResourceKey<T>, Exception> rdpr$capturedExceptionMap;
    @Unique
    private WritableRegistry<T> rdpr$writeableRegistry;

    @Inject(method = "create", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void rdpr$captureExceptionMap(Lifecycle lifecycle, Map<ResourceKey<T>, Exception> exceptionMap, CallbackInfoReturnable<Pair<WritableRegistry<T>, RegistryDataLoader.Loader>> cir, WritableRegistry<T> writableRegistry) {
        this.rdpr$capturedExceptionMap = exceptionMap;
        this.rdpr$writeableRegistry = writableRegistry;
    }

    @ModifyArg(method = "create", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;"), index = 1)
    private Object rdpr$createData(Object original) {
        if (ReloadableDatapackRegistries.isReloadableRegistry(this.key())) {
            ReloadableRegistryData<?> reloadableRegistryData = ReloadableDatapackRegistries.getReloadableRegistryData(this.key());
            if (reloadableRegistryData.getDataLoader().isPresent()) {
                return (RegistryDataLoader.Loader) (resourceManager, registryInfoLookup) -> {
                    ReloadableDatapackRegistries.getReloadableRegistryData(this.key()).getDataLoader().get().load(registryInfoLookup, resourceManager, rdpr$writeableRegistry, rdpr$capturedExceptionMap);
                    rdpr$capturedExceptionMap = null;
                    rdpr$writeableRegistry = null;
                };
            }
        }
        return original;
    }

}
