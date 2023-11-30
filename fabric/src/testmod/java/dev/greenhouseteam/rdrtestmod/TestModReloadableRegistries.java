package dev.greenhouseteam.rdrtestmod;

import dev.greenhouseteam.reloadabledatapackregistries.ReloadableRegistryCreationHelper;
import dev.greenhouseteam.reloadabledatapackregistries.ReloadableRegistryEntryPoint;

public class TestModReloadableRegistries implements ReloadableRegistryEntryPoint {
    @Override
    public void createContents(ReloadableRegistryCreationHelper helper) {
        helper.setupReloadableRegistry(RDRTestMod.BASIC_RECORD, BasicRecord.CODEC);
        helper.setupReloadableRegistry(RDRTestMod.LOG_REGISTRY, BasicRecord.CODEC, null);
        helper.setCustomDataLoader(RDRTestMod.LOG_REGISTRY, RDRTestMod.logCustomDataLoader());
    }
}
