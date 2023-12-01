package dev.greenhouseteam.rdpr.api.platform;

import com.google.gson.JsonElement;
import com.mojang.serialization.Decoder;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public interface IRDPRApiPlatformHelper {

    IRDPRApiPlatformHelper INSTANCE = ServiceUtil.load(IRDPRApiPlatformHelper.class);

    <T> void fromExistingRegistry(IReloadableRegistryCreationHelper helper, ResourceKey<Registry<T>> registryKey);

    RegistryOps<JsonElement> getRegistryOps(RegistryOps.RegistryInfoLookup lookup);

    <T> Decoder<Optional<T>> getDecoder(Decoder<T> original);

}
