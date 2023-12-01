package dev.greenhouseteam.reloadabledatapackregistries.api.platform;

import com.google.gson.JsonElement;
import com.mojang.serialization.Decoder;
import net.minecraft.resources.RegistryOps;

import java.util.Optional;

public interface IRDRApiPlatformHelper {

    IRDRApiPlatformHelper INSTANCE = ServiceUtil.load(IRDRApiPlatformHelper.class);

    RegistryOps<JsonElement> getRegistryOps(RegistryOps.RegistryInfoLookup lookup);

    <T> Decoder<Optional<T>> getDecoder(Decoder<T> original);

}
