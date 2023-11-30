package dev.greenhouseteam.reloadabledatapackregistries.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.reloadabledatapackregistries.ReloadableRegistryCreationHelper;
import dev.greenhouseteam.reloadabledatapackregistries.ReloadableRegistryEntryPoint;
import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadRegistriesClientboundPacket;
import dev.greenhouseteam.reloadabledatapackregistries.platform.services.IRDRPlatformHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

@AutoService(IRDRPlatformHelper.class)
public class FabricRDRPlatformHelper implements IRDRPlatformHelper {

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
        FabricLoader.getInstance().getEntrypoints("reloadable_datapack_registries", ReloadableRegistryEntryPoint.class).forEach(entryPoint -> entryPoint.createContents(ReloadableRegistryCreationHelper.INSTANCE));
    }

    @Override
    public void sendReloadPacket(ReloadRegistriesClientboundPacket packet, List<ServerPlayer> serverPlayers) {
        for (ServerPlayer player : serverPlayers) {
            FriendlyByteBuf buf = packet.toBuf();
            ServerPlayNetworking.send(player, packet.getFabricId(), buf);
        }
    }
}
