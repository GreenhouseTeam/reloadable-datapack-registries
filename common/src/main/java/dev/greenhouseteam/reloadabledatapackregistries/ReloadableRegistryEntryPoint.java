package dev.greenhouseteam.reloadabledatapackregistries;

public interface ReloadableRegistryEntryPoint {
    /**
     * Called at init to create reloadable registries.
     *
     * @param helper A helper with methods relating to creation.
     */
    void createContents(ReloadableRegistryCreationHelper helper);
}
