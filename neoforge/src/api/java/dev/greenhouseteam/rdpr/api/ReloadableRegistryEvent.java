package dev.greenhouseteam.rdpr.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * An event for allowing your data pack registries (registered through {@link net.neoforged.neoforge.registries.DataPackRegistryEvent})
 * to reload upon using /relaod.
 */
public class ReloadableRegistryEvent extends Event implements IReloadableRegistryCreationHelper, IModBusEvent {
    private final List<Data<?>> data = new ArrayList<>();

    @ApiStatus.Internal
    public ReloadableRegistryEvent() {}

    /**
     * Makes a registry reloadable using the specified codec, and a specified network codec.
     * This does not modify the registry when the world is loaded.
     *
     * @param registryKey   The {@link ResourceKey} of the registry to make reloadable.
     * @param codec         The codec to use for loading.
     * @param networkCodec  The codec to use for loading over the network.
     * @param <T>           The type of the registry.
     */
    @Override
    public <T> void registerNetworkableReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec) {
        data.add(new Data<>(registryKey, codec, networkCodec));
    }

    @ApiStatus.Internal
    public void post(IReloadableRegistryCreationHelper helper) {
        for (Data<?> d : data) {
            handleData(d, helper);
        }
    }

    private <T> void handleData(Data<T> data, IReloadableRegistryCreationHelper helper) {
        helper.registerNetworkableReloadableRegistry(data.registryKey, data.reloadableRegistryData, data.networkableRegistryData);
    }

    private static class Data<T> {
        private final ResourceKey<Registry<T>> registryKey;
        private final Codec<T> reloadableRegistryData;
        private final Codec<T> networkableRegistryData;
        protected Data(ResourceKey<Registry<T>> registryKey, Codec<T> reloadableRegistryData, Codec<T> networkableRegistryData) {
            this.registryKey = registryKey;
            this.reloadableRegistryData = reloadableRegistryData;
            this.networkableRegistryData = networkableRegistryData;
        }

    }

}
