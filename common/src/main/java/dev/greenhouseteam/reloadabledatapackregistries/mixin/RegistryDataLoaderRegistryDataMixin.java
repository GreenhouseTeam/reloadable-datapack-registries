package dev.greenhouseteam.reloadabledatapackregistries.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import dev.greenhouseteam.reloadabledatapackregistries.CustomDataLoader;
import dev.greenhouseteam.reloadabledatapackregistries.access.CustomDataLoaderAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.Optional;

@Mixin(RegistryDataLoader.RegistryData.class)
public abstract class RegistryDataLoaderRegistryDataMixin<T> implements CustomDataLoaderAccess {

    @Unique
    private Map<ResourceKey<?>, Exception> reloadabledatapackregistries$capturedExceptionMap;
    @Unique
    private WritableRegistry<T> reloadabledatapackregistries$writeableRegistry;
    @Unique
    private Optional<CustomDataLoader> reloadabledatapackregistries$customDataLoader = Optional.empty();

    @Inject(method = "create", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void reloadabledatapackregistries$captureExceptionMap(Lifecycle lifecycle, Map<ResourceKey<?>, Exception> exceptionMap, CallbackInfoReturnable<Pair<WritableRegistry<?>, RegistryDataLoader.Loader>> cir, WritableRegistry<T> writableRegistry) {
        this.reloadabledatapackregistries$capturedExceptionMap = exceptionMap;
        this.reloadabledatapackregistries$writeableRegistry = writableRegistry;
    }

    @ModifyArg(method = "create", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;"), index = 1)
    private Object reloadabledatapackregistries$createData(Object original) {
        if (reloadabledatapackregistries$customDataLoader.isPresent()) {
            return (RegistryDataLoader.Loader) (resourceManager, registryInfoLookup) -> {
                reloadabledatapackregistries$customDataLoader.get().load(registryInfoLookup, resourceManager, reloadabledatapackregistries$writeableRegistry, reloadabledatapackregistries$capturedExceptionMap);
                reloadabledatapackregistries$capturedExceptionMap = null;
                reloadabledatapackregistries$writeableRegistry = null;
            };
        }
        return original;
    }

    public void reloadabledatapackregistries$setLoadFunction(Optional<CustomDataLoader> loader) {
        this.reloadabledatapackregistries$customDataLoader = loader;
    }

}
