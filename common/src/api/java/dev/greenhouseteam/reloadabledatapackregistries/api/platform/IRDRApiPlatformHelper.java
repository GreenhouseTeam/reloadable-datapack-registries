package dev.greenhouseteam.reloadabledatapackregistries.api.platform;

import com.google.gson.JsonElement;
import com.mojang.serialization.Decoder;
import dev.greenhouseteam.reloadabledatapackregistries.api.IReloadableRegistryCreationHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public interface IRDRApiPlatformHelper {

    IRDRApiPlatformHelper INSTANCE = ServiceUtil.load(IRDRApiPlatformHelper.class);

    <T> void fromExistingRegistry(IReloadableRegistryCreationHelper helper, ResourceKey<Registry<T>> registryKey);

    RegistryOps<JsonElement> getRegistryOps(RegistryOps.RegistryInfoLookup lookup);

    <T> Decoder<Optional<T>> getDecoder(Decoder<T> original);

}
