package dev.greenhouseteam.reloadabledatapackregistries.network;

import dev.greenhouseteam.reloadabledatapackregistries.impl.ReloadableDatapackRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class ReloadableDatapackRegistriesNetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ReloadableDatapackRegistries.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int i = 0;
        INSTANCE.registerMessage(++i, ReloadRegistriesClientboundPacket.class, ReloadRegistriesClientboundPacket::encode, ReloadRegistriesClientboundPacket::decode, (packet, context) -> {
            packet.handle();
            context.setPacketHandled(true);
        });
    }

}
