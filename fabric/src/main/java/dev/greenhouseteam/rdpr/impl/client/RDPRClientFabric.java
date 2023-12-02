package dev.greenhouseteam.rdpr.impl.client;

import dev.greenhouseteam.rdpr.impl.network.ReloadableDatapackRegistriesPackets;
import net.fabricmc.api.ClientModInitializer;

public class RDPRClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ReloadableDatapackRegistriesPackets.initClient();
    }
}
