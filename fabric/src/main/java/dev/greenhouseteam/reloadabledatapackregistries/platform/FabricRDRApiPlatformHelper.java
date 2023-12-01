package dev.greenhouseteam.reloadabledatapackregistries.platform;

import com.google.auto.service.AutoService;
import com.google.gson.JsonElement;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import dev.greenhouseteam.reloadabledatapackregistries.api.platform.IRDRApiPlatformHelper;
import net.minecraft.resources.RegistryOps;

import java.util.Optional;

@AutoService(IRDRApiPlatformHelper.class)
public class FabricRDRApiPlatformHelper implements IRDRApiPlatformHelper {

    @Override
    public RegistryOps<JsonElement> getRegistryOps(RegistryOps.RegistryInfoLookup lookup) {
        return RegistryOps.create(JsonOps.INSTANCE, lookup);
    }

    @Override
    public <T> Decoder<Optional<T>> getDecoder(Decoder<T> original) {
        return original.map(Optional::of);
    }
}
