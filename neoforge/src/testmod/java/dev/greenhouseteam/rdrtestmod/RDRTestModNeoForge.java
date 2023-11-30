package dev.greenhouseteam.rdrtestmod;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod("rdrtestmod")
public class RDRTestModNeoForge {
    public RDRTestModNeoForge() {
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> TestCommand.register(event.getDispatcher(), event.getBuildContext()));
    }
}
