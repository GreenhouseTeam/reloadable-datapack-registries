package dev.greenhouseteam.reloadabledatapackregistries.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPacketListener.class)
public interface ClientPacketListenerAccessor {
    @Accessor("registryAccess") @Mutable
    void reloadabledatapackregistries$setRegistryAccess(RegistryAccess.Frozen value);
}
