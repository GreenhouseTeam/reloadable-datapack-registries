package dev.greenhouseteam.rdpr.mixin;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Mixin(LayeredRegistryAccess.class)
public interface LayeredRegistryAccessAccessor<T> {
    @Accessor("keys")
    List<T> reloadabledatapackregistries$getKeys();

    @Accessor("values")
    List<RegistryAccess.Frozen> reloadabledatapackregistries$getValues();

    @Accessor("values") @Mutable @Final
    void reloadabledatapackregistries$setValues(List<RegistryAccess.Frozen> value);

    @Accessor("composite") @Mutable @Final
    void reloadabledatapackregistries$setComposite(RegistryAccess.Frozen value);

    @Invoker("collectRegistries")
    static Map<ResourceKey<? extends Registry<?>>, Registry<?>> reloadabledatapackregistries$invokeCollectRegistries(Stream<? extends RegistryAccess> $$0) {
        throw new RuntimeException("");
    }
}
