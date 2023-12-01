package dev.greenhouseteam.rdrtestmod;

import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.entrypoint.ReloadableRegistryPlugin;

public class RDRTestModEntrypoint implements ReloadableRegistryPlugin {
    @Override
    public void createContents(IReloadableRegistryCreationHelper helper) {
        TestModReloadableRegistries.createContents(helper);
    }
}
