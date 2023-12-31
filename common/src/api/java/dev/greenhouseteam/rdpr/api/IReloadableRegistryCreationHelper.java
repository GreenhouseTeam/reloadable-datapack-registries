package dev.greenhouseteam.rdpr.api;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.rdpr.api.platform.IRDPRApiPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IReloadableRegistryCreationHelper {

    /**
     * Makes a registry reloadable with the same codec and network codec (if present).
     * but only if it is a pre-existing datapackable registry.
     * This will do nothing if the registry is not one.
     *
     * @param registryKey   The {@link ResourceKey} of the registry to make reloadable.
     * @param <T>           The type of the registry.
     */
    default <T> void fromExistingRegistry(ResourceKey<Registry<T>> registryKey) {
        IRDPRApiPlatformHelper.INSTANCE.fromExistingRegistry(this, registryKey);
    }

    /**
     * Makes a registry reloadable using the specified codec.
     * This does not modify the registry when the world is loaded.
     *
     * @param registryKey   The {@link ResourceKey} of the registry to make reloadable.
     * @param codec         The codec to use as the loading and network codec.
     * @param <T>           The type of the registry.
     */
    default <T> void registerReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        registerNetworkableReloadableRegistry(registryKey, codec, null);
    }

    /**
     * Makes a registry reloadable using the specified codec and as a network codec too.
     * This does not modify the registry when the world is loaded.
     *
     * @param registryKey   The {@link ResourceKey} of the registry to make reloadable.
     * @param codec         The codec to use as the loading and network codec.
     * @param <T>           The type of the registry.
     */
    default <T> void registerNetworkableReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        registerNetworkableReloadableRegistry(registryKey, codec, codec);
    }

    /**
     * Makes a registry reloadable using the specified codec, and a specified network codec.
     * This does not modify the registry when the world is loaded.
     *
     * @param registryKey   The {@link ResourceKey} of the registry to make reloadable.
     * @param codec         The codec to use for loading.
     * @param networkCodec  The codec to use for loading over the network.
     * @param <T>           The type of the registry.
     */
    <T> void registerNetworkableReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec);

}
