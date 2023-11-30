package dev.greenhouseteam.reloadabledatapackregistries.client;

import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadableDatapackRegistriesPackets;
import net.fabricmc.api.ClientModInitializer;

public class ReloadableDatapackRegistriesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ReloadableDatapackRegistriesPackets.initClient();
    }
}
