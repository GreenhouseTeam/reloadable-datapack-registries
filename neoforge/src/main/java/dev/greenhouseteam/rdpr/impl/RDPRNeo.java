package dev.greenhouseteam.rdpr.impl;

import dev.greenhouseteam.rdpr.impl.network.ReloadableDatapackRegistriesNetworkHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ReloadableDatapackRegistries.MOD_ID)
public class RDPRNeo {

    public RDPRNeo() {}

    @Mod.EventBusSubscriber(modid = ReloadableDatapackRegistries.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ForgeEvents {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            ReloadableDatapackRegistriesNetworkHandler.init();
            ReloadableDatapackRegistries.init();
        }
    }
}
