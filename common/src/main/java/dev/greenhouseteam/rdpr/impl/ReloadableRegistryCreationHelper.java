package dev.greenhouseteam.rdpr.impl;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.ReloadableRegistryData;
import dev.greenhouseteam.rdpr.api.loader.CustomDataLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

@AutoService(IReloadableRegistryCreationHelper.class)
public class ReloadableRegistryCreationHelper implements IReloadableRegistryCreationHelper {
    @Override
    public <T> void registerNetworkableReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networKCodec) {
        ReloadableRegistryData<T> registryData = new ReloadableRegistryData<>(new RegistryDataLoader.RegistryData<>(registryKey, codec));

        ReloadableDatapackRegistries.RELOADABLE_REGISTRY_DATA.put(registryKey, registryData);
        if (networKCodec != null) {
            ReloadableDatapackRegistries.NETWORKABLE_REGISTRIES.put(registryKey, new RegistrySynchronization.NetworkedRegistryData<>(registryKey, networKCodec));
        }
    }

    @Override
    public <T> void setCustomDataLoader(ResourceKey<Registry<T>> resourceKey, CustomDataLoader<T> customDataLoader) {
        ReloadableDatapackRegistries.getReloadableRegistryData(resourceKey).setCustomDataLoader(customDataLoader);
    }

}
