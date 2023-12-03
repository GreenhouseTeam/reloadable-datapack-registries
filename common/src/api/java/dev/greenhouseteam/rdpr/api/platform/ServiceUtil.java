package dev.greenhouseteam.rdpr.api.platform;

import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

public class ServiceUtil {

    public static <T> T load(Class<T> clazz) {

        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}