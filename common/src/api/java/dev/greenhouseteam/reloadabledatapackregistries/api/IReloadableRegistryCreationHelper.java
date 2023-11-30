package dev.greenhouseteam.reloadabledatapackregistries.api;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.reloadabledatapackregistries.api.loader.CustomDataLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IReloadableRegistryCreationHelper {
    <T> void setupReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec);

    <T> void setupReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networKCodec);

    <T> void before(ResourceKey<Registry<T>> resourceKey, ResourceKey<Registry<?>> otherRegistry);

    <T> void after(ResourceKey<Registry<T>> resourceKey, ResourceKey<Registry<?>> otherRegistry);

    <T> void setCustomDataLoader(ResourceKey<Registry<T>> resourceKey, CustomDataLoader<T> customDataLoader);

}
