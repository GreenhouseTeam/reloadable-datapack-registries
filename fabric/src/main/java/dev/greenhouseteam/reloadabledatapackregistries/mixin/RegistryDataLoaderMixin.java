package dev.greenhouseteam.reloadabledatapackregistries.mixin;

import dev.greenhouseteam.reloadabledatapackregistries.ReloadableDatapackRegistries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Inject(method = "registryDirPath", at = @At("HEAD"), cancellable = true)
    private static void reloadabledatapackregistries$createRegistryDirPath(ResourceLocation resource, CallbackInfoReturnable<String> cir) {
        if (!resource.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) && ReloadableDatapackRegistries.isReloadableRegistry(ResourceKey.createRegistryKey(resource)))
            cir.setReturnValue(resource.getNamespace() + "/" + resource.getPath());
    }
}
