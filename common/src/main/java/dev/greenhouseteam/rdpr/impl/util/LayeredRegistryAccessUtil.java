package dev.greenhouseteam.rdpr.impl.util;

import dev.greenhouseteam.rdpr.mixin.LayeredRegistryAccessAccessor;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;

import java.util.ArrayList;
import java.util.List;

public class LayeredRegistryAccessUtil {
    public static void replaceSpecificLayer(LayeredRegistryAccess<RegistryLayer> access, RegistryLayer layer, RegistryAccess.Frozen newLayer) {
        List<RegistryAccess.Frozen> oldRegistries = ((LayeredRegistryAccessAccessor<RegistryLayer>)access).rdpr$getValues();
        List<RegistryAccess.Frozen> newRegistries = new ArrayList<>();

        for (int i = 0; i < oldRegistries.size(); ++i) {
            if (i == ((LayeredRegistryAccessAccessor<RegistryLayer>)access).rdpr$getKeys().indexOf(layer))
                newRegistries.add(newLayer);
            else
                newRegistries.add(oldRegistries.get(i));
        }

        ((LayeredRegistryAccessAccessor<RegistryLayer>)access).rdpr$setValues(List.copyOf(newRegistries));
        ((LayeredRegistryAccessAccessor<RegistryLayer>)access).rdpr$setComposite(new RegistryAccess.ImmutableRegistryAccess(LayeredRegistryAccessAccessor.rdpr$invokeCollectRegistries(newRegistries.stream())).freeze());
    }
}
