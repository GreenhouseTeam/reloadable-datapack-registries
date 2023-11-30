package dev.greenhouseteam.reloadabledatapackregistries.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ReloadableDatapackRegistriesPackets {

    public static void initClient() {
        ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(ReloadRegistriesClientboundPacket.ID, (client, handler, buf, responseSender) -> {
                ReloadRegistriesClientboundPacket packet = ReloadRegistriesClientboundPacket.decode(buf);
                packet.handle();
            });
        });
    }
}
