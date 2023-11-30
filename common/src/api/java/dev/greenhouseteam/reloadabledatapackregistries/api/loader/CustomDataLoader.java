package dev.greenhouseteam.reloadabledatapackregistries.api.loader;

import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;

/**
 * An interface that allows you to override the default Minecraft logic for loading
 * data pack registries.
 */
@FunctionalInterface
public interface CustomDataLoader<T> {
    void load(RegistryOps.RegistryInfoLookup lookup, ResourceManager resourceManager, WritableRegistry<T> writableRegistry, Map<ResourceKey<T>, Exception> exceptionMap);
}
