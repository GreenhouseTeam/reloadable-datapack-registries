package dev.greenhouseteam.rdpr.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.rdpr.api.entrypoint.ReloadableRegistryPlugin;
import dev.greenhouseteam.rdpr.impl.ReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.network.ReloadRegistriesClientboundPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

@AutoService(IRDPRPlatformHelper.class)
public class FabricRDPRPlatformHelper implements IRDPRPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void invokeEntrypoints() {
        FabricLoader.getInstance().getEntrypoints("rdpr", ReloadableRegistryPlugin.class).forEach(entryPoint -> entryPoint.createContents(ReloadableRegistryCreationHelper.INSTANCE));
    }

    @Override
    public void sendReloadPacket(ReloadRegistriesClientboundPacket packet, List<ServerPlayer> serverPlayers) {
        for (ServerPlayer player : serverPlayers) {
            FriendlyByteBuf buf = packet.toBuf();
            ServerPlayNetworking.send(player, packet.getFabricId(), buf);
        }
    }
}
