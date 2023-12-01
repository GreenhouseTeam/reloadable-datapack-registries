package dev.greenhouseteam.reloadabledatapackregistries.api;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.reloadabledatapackregistries.api.loader.CustomDataLoader;
import dev.greenhouseteam.reloadabledatapackregistries.api.platform.ServiceUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IReloadableRegistryCreationHelper {

    IReloadableRegistryCreationHelper INSTANCE = ServiceUtil.load(IReloadableRegistryCreationHelper.class);

    default <T> void registerReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        registerReloadableRegistry(registryKey, codec, null);
    }

    <T> void registerReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networKCodec);

    <T> void setCustomDataLoader(ResourceKey<Registry<T>> registryKey, CustomDataLoader<T> customDataLoader);
}
