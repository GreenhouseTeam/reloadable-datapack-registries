package dev.greenhouseteam.rdpr.impl.client;

import dev.greenhouseteam.rdpr.network.ReloadableDatapackRegistriesPackets;
import net.fabricmc.api.ClientModInitializer;

public class ReloadableDatapackRegistriesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ReloadableDatapackRegistriesPackets.initClient();
    }
}
