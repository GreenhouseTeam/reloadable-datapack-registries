package dev.greenhouseteam.reloadabledatapackregistries.impl;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import dev.greenhouseteam.reloadabledatapackregistries.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.reloadabledatapackregistries.api.loader.CustomDataLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

@AutoService(IReloadableRegistryCreationHelper.class)
public class ReloadableRegistryCreationHelper implements IReloadableRegistryCreationHelper {
    public static final IReloadableRegistryCreationHelper INSTANCE = new ReloadableRegistryCreationHelper();

    public <T> void setupReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        setupReloadableRegistry(registryKey, codec, codec);
    }

    public <T> void setupReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networKCodec) {
        ReloadableRegistryData<T> registryData = new ReloadableRegistryData<>(new RegistryDataLoader.RegistryData<>(registryKey, codec));

        ReloadableDatapackRegistries.RELOADABLE_REGISTRY_DATA.put(registryKey, registryData);
        if (networKCodec != null) {
            RegistrySynchronization.NetworkedRegistryData<T> networkedRegistryData = new RegistrySynchronization.NetworkedRegistryData<>(registryKey, networKCodec);
            ReloadableDatapackRegistries.NETWORKABLE_REGISTRIES.put(registryKey, networkedRegistryData);
            ReloadableDatapackRegistries.addNetworkCodecToMap(registryKey, networkedRegistryData);
        }
    }

    public <T> void setCustomDataLoader(ResourceKey<Registry<T>> resourceKey, CustomDataLoader<T> customDataLoader) {
        ReloadableDatapackRegistries.getReloadableRegistryData(resourceKey).setCustomDataLoader(customDataLoader);
    }

}
