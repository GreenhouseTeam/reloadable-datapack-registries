package dev.greenhouseteam.rdpr.api.entrypoint;

import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;

/**
 * Called at init to create reloadable registries.
 * To load this on Fabric or Quilt, you'll have to add your plugin class to the 'reloadabledatapackregistries' entrypoint.
 * To load this on NeoForge, use the ReloadableRegistryEvent on the mod event bus.
 */
public interface ReloadableRegistryPlugin {
    /**
     */
    void createContents(IReloadableRegistryCreationHelper helper);
}
