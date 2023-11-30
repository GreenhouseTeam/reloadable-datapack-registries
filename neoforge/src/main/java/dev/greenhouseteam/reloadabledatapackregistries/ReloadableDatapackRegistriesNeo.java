package dev.greenhouseteam.reloadabledatapackregistries;


import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadableDatapackRegistriesNetworkHandler;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ReloadableDatapackRegistries.MOD_ID)
public class ReloadableDatapackRegistriesNeo {

    public ReloadableDatapackRegistriesNeo() {
        NeoForge.EVENT_BUS.addListener(FMLCommonSetupEvent.class, event -> {
            ReloadableDatapackRegistriesNetworkHandler.init();
        });
    }
}