package dev.greenhouseteam.rdprtestmod;

import dev.greenhouseteam.rdprtestmod.record.BasicRecord;
import dev.greenhouseteam.rdprtestmod.record.Chocolate;
import dev.greenhouseteam.rdprtestmod.record.LogRecord;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class RDPRTestModFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(TestModReloadableRegistries.BASIC_RECORD, BasicRecord.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        DynamicRegistries.register(TestModReloadableRegistries.LOG_REGISTRY, LogRecord.CODEC);
        DynamicRegistries.registerSynced(TestModReloadableRegistries.CHOCOLATE_REGISTRY, Chocolate.DIRECT_CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> RDPRCommand.register(dispatcher, context));
    }
}
