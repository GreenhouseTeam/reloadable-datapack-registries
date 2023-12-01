package dev.greenhouseteam.rdrtestmod;

import dev.greenhouseteam.rdrtestmod.record.BasicRecord;
import dev.greenhouseteam.rdrtestmod.record.Chocolate;
import dev.greenhouseteam.rdrtestmod.record.LogRecord;
import dev.greenhouseteam.reloadabledatapackregistries.api.ReloadableRegistryEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod("rdrtestmod")
public class RDRTestModNeoForge {
    public RDRTestModNeoForge() {
    }

    @Mod.EventBusSubscriber(modid = "rdrtestmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            RDRTestCommand.register(event.getDispatcher(), event.getBuildContext());
        }
    }

    @Mod.EventBusSubscriber(modid = "rdrtestmod", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void createNewDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(TestModReloadableRegistries.BASIC_RECORD, BasicRecord.CODEC, BasicRecord.CODEC);
            event.dataPackRegistry(TestModReloadableRegistries.LOG_REGISTRY, LogRecord.CODEC);
            event.dataPackRegistry(TestModReloadableRegistries.CHOCOLATE_REGISTRY, Chocolate.DIRECT_CODEC, Chocolate.DIRECT_CODEC);
        }

        @SubscribeEvent
        public static void addReloadableRegistries(ReloadableRegistryEvent event) {
            TestModReloadableRegistries.createContents(event);
        }
    }
}
