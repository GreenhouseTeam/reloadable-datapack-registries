package dev.greenhouseteam.rdpr.impl;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

public class ReloadableRegistryCreationHelper implements IReloadableRegistryCreationHelper {

    public static final IReloadableRegistryCreationHelper INSTANCE = new ReloadableRegistryCreationHelper();

    @Override
    public <T> void registerNetworkableReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec) {
        RegistryDataLoader.RegistryData<T> registryData = new RegistryDataLoader.RegistryData<>(registryKey, codec);

        ReloadableDatapackRegistries.RELOADABLE_REGISTRY_DATA.put(registryKey, registryData);
        if (networkCodec != null) {
            ReloadableDatapackRegistries.NETWORKABLE_REGISTRIES.put(registryKey, new RegistrySynchronization.NetworkedRegistryData<>(registryKey, networkCodec));
        }
    }

}
