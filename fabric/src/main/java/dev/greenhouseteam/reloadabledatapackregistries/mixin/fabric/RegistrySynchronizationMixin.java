package dev.greenhouseteam.reloadabledatapackregistries.mixin.fabric;

import com.google.common.collect.ImmutableMap;
import dev.greenhouseteam.reloadabledatapackregistries.ReloadableDatapackRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RegistrySynchronization.class)
public class RegistrySynchronizationMixin {
    @Inject(method = "method_45958", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void reloadabledatapackregistries$addToNetworkableCodecs(CallbackInfoReturnable<ImmutableMap> cir, ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder) {
        ReloadableDatapackRegistries.populateWithNetworkCodecs(builder);
    }

}
