package dev.greenhouseteam.reloadabledatapackregistries.access;

import dev.greenhouseteam.reloadabledatapackregistries.CustomDataLoader;

import java.util.Optional;

public interface CustomDataLoaderAccess {
    void reloadabledatapackregistries$setLoadFunction(Optional<CustomDataLoader> loader);
}
