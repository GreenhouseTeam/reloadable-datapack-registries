package dev.greenhouseteam.reloadabledatapackregistries.api.entrypoint;

import dev.greenhouseteam.reloadabledatapackregistries.api.IReloadableRegistryCreationHelper;

/**
 * Called at init to create reloadable registries.
 * To load this on Fabric or Quilt, you'll have to add your plugin class to the 'reloadabledatapackregistries' entrypoint.
 * To load this on NeoForge, attach the {@link ReloadableRegistryEntrypoint} annotation to your plugin class.
 */
public interface ReloadableRegistryPlugin {
    /**
     */
    void createContents(IReloadableRegistryCreationHelper helper);
}
