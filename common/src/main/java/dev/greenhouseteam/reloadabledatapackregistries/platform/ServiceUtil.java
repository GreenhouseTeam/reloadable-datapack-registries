package dev.greenhouseteam.reloadabledatapackregistries.platform;

import dev.greenhouseteam.reloadabledatapackregistries.impl.ReloadableDatapackRegistries;

import java.util.ServiceLoader;

public class ServiceUtil {
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        ReloadableDatapackRegistries.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}