package dev.greenhouseteam.rdpr.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.greenhouseteam.rdpr.impl.ReloadableDatapackRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistrySynchronization.class)
public class RegistrySynchronizationMixin {

    @Inject(method = "getNetworkCodec", at = @At("RETURN"), cancellable = true)
    private static <E> void reloadabledatapackregistries$getReloadableNetworkCodec(ResourceKey<? extends Registry<E>> registryKey, CallbackInfoReturnable<DataResult<? extends Codec<E>>> cir) {
        if (ReloadableDatapackRegistries.isNetworkable(registryKey))
            cir.setReturnValue(ReloadableDatapackRegistries.getNetworkCodec(registryKey));
    }

}
