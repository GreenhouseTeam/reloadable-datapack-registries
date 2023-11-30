package dev.greenhouseteam.rdrtestmod;

import dev.greenhouseteam.reloadabledatapackregistries.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.reloadabledatapackregistries.api.entrypoint.ReloadableRegistryPlugin;

public class TestModReloadableRegistriesPlugin implements ReloadableRegistryPlugin {
    @Override
    public void createContents(IReloadableRegistryCreationHelper helper) {
        helper.setupReloadableRegistry(RDRTestMod.BASIC_RECORD, BasicRecord.CODEC);
        helper.setupReloadableRegistry(RDRTestMod.LOG_REGISTRY, BasicRecord.CODEC, null);
        helper.setCustomDataLoader(RDRTestMod.LOG_REGISTRY, RDRTestMod.logCustomDataLoader());
    }
}
