package dev.greenhouseteam.reloadabledatapackregistries.impl.client;

import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadableDatapackRegistriesPackets;
import net.fabricmc.api.ClientModInitializer;

public class ReloadableDatapackRegistriesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ReloadableDatapackRegistriesPackets.initClient();
    }
}
