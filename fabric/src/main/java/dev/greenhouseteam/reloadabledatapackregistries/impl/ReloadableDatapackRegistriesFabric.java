package dev.greenhouseteam.reloadabledatapackregistries.impl;

import net.fabricmc.api.ModInitializer;

public class ReloadableDatapackRegistriesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ReloadableDatapackRegistries.init();
    }
}
