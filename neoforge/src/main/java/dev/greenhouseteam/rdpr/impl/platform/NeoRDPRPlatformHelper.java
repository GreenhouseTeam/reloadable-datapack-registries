package dev.greenhouseteam.rdpr.impl.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.ReloadableRegistryEvent;
import dev.greenhouseteam.rdpr.impl.ReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.impl.network.ReloadRegistriesClientboundPacket;
import dev.greenhouseteam.rdpr.impl.network.ReloadableDatapackRegistriesNetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@AutoService(IRDPRPlatformHelper.class)
public class NeoRDPRPlatformHelper implements IRDPRPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void invokeEntrypoints() {
        ReloadableRegistryEvent reloadableRegistryEvent = new ReloadableRegistryEvent();
        ModLoader.get().postEvent(reloadableRegistryEvent);
        reloadableRegistryEvent.post(ReloadableRegistryCreationHelper.INSTANCE);
    }

    @Override
    public void sendReloadPacket(ReloadRegistriesClientboundPacket packet, List<ServerPlayer> serverPlayers) {
        ReloadableDatapackRegistriesNetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

}