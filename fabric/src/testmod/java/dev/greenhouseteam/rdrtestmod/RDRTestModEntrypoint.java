package dev.greenhouseteam.rdrtestmod;

import dev.greenhouseteam.reloadabledatapackregistries.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.reloadabledatapackregistries.api.entrypoint.ReloadableRegistryPlugin;

public class RDRTestModEntrypoint implements ReloadableRegistryPlugin {
    @Override
    public void createContents(IReloadableRegistryCreationHelper helper) {
        TestModReloadableRegistries.createContents(helper);
    }
}
