package dev.greenhouseteam.reloadabledatapackregistries.impl;

import dev.greenhouseteam.reloadabledatapackregistries.api.loader.CustomDataLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;

import java.text.Collator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ReloadableRegistryData<T> {
    private final RegistryDataLoader.RegistryData<T> registryData;
    private Optional<CustomDataLoader<T>> dataLoader = Optional.empty();

    public void setCustomDataLoader(CustomDataLoader<T> loader) {
        if (this.dataLoader.isPresent()) {
            throw new UnsupportedOperationException("ReloadableRegistryData for '" + this.getRegistryData().key() + "' already has a CustomDataLoader implementation.");
        }
        this.dataLoader = Optional.of(loader);
    }

    public ReloadableRegistryData(RegistryDataLoader.RegistryData<T> registryData) {
        this.registryData = registryData;
    }

    public RegistryDataLoader.RegistryData<T> getRegistryData() {
        return this.registryData;
    }

    public Optional<CustomDataLoader<T>> getDataLoader() {
        return this.dataLoader;
    }

}
