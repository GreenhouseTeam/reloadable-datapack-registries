package dev.greenhouseteam.reloadabledatapackregistries.impl;

import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadableDatapackRegistriesNetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ReloadableDatapackRegistries.MOD_ID)
public class ReloadableDatapackRegistriesNeo {
    public ReloadableDatapackRegistriesNeo(IEventBus eventBus) {
        ReloadableDatapackRegistries.init();
        eventBus.addListener(FMLCommonSetupEvent.class, event -> {
            ReloadableDatapackRegistriesNetworkHandler.init();
        });
    }
}
