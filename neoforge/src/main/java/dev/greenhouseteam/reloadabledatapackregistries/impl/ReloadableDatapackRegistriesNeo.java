package dev.greenhouseteam.reloadabledatapackregistries.impl;

import dev.greenhouseteam.reloadabledatapackregistries.api.ReloadableRegistryEvent;
import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadableDatapackRegistriesNetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ReloadableDatapackRegistries.MOD_ID)
public class ReloadableDatapackRegistriesNeo {

    public ReloadableDatapackRegistriesNeo(IEventBus bus) {}

    @Mod.EventBusSubscriber(modid = ReloadableDatapackRegistries.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ForgeEvents {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            ReloadableDatapackRegistriesNetworkHandler.init();
            ReloadableDatapackRegistries.init();
        }
    }
}
