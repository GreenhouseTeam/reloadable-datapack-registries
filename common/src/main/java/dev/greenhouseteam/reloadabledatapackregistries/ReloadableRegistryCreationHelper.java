package dev.greenhouseteam.reloadabledatapackregistries;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.reloadabledatapackregistries.platform.services.IRDRPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

public class ReloadableRegistryCreationHelper {
    public static final ReloadableRegistryCreationHelper INSTANCE = new ReloadableRegistryCreationHelper();

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

    public <T> void before(ResourceKey<Registry<T>> resourceKey, ResourceKey<Registry<?>> otherRegistry) {
        ReloadableDatapackRegistries.getReloadableRegistryData(resourceKey).before(otherRegistry);
    }

    public <T> void after(ResourceKey<Registry<T>> resourceKey, ResourceKey<Registry<?>> otherRegistry) {
        ReloadableDatapackRegistries.getReloadableRegistryData(resourceKey).after(otherRegistry);
    }

    public <T> void setCustomDataLoader(ResourceKey<Registry<T>> resourceKey, CustomDataLoader<T> customDataLoader) {
        ReloadableDatapackRegistries.getReloadableRegistryData(resourceKey).setCustomDataLoader(customDataLoader);
    }

}
