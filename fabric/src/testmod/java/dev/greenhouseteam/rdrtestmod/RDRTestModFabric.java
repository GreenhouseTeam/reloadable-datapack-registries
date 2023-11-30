package dev.greenhouseteam.rdrtestmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class RDRTestModFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> TestCommand.register(dispatcher, context));
    }
}
