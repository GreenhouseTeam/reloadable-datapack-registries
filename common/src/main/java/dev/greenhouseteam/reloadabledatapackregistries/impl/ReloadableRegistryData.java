package dev.greenhouseteam.reloadabledatapackregistries.impl;

import dev.greenhouseteam.reloadabledatapackregistries.api.loader.CustomDataLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ReloadableRegistryData<T> {

    private final RegistryDataLoader.RegistryData<T> registryData;
    private final Set<ResourceKey<? extends Registry<?>>> loadBeforeSet = new HashSet<>();
    private final Set<ResourceKey<? extends Registry<?>>> loadAfterSet = new HashSet<>();
    private Optional<CustomDataLoader<T>> dataLoader = Optional.empty();

    public int compareTo(ReloadableRegistryData<?> other) {
        int i = this.loadBeforeSet.contains(other.getRegistryData().key()) || other.loadAfterSet.contains(this.getRegistryData().key()) ? -1 : 0;

        i = i != 0 ? i : this.loadAfterSet.contains(other.getRegistryData().key()) || other.loadBeforeSet.contains(this.getRegistryData().key()) ? 1 : 0;

        i = i != 0 ? i : this.getRegistryData().key().location().compareTo(other.getRegistryData().key().location());

        return i;
    }

    public void setCustomDataLoader(CustomDataLoader<T> loader) {
        if (this.dataLoader.isPresent()) {
            throw new UnsupportedOperationException("ReloadableRegistryData for '" + this.getRegistryData().key() + "' already has a CustomDataLoader implementation.");
        }
        this.dataLoader = Optional.of(loader);
    }

    public void before(ResourceKey<Registry<?>> registryKey) {
        this.loadBeforeSet.add(registryKey);
    }


    public void after(ResourceKey<Registry<?>> registryKey) {
        this.loadAfterSet.add(registryKey);
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
