package dev.greenhouseteam.rdpr.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Level.class)
public interface LevelAccessor {
    @Accessor("registryAccess") @Mutable @Final
    void rdpr$setRegistryAccess(RegistryAccess value);
}