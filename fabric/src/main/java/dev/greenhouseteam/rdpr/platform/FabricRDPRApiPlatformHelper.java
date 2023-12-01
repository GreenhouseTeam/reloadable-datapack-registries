package dev.greenhouseteam.rdpr.platform;

import com.google.auto.service.AutoService;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.platform.IRDPRApiPlatformHelper;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.Optional;

@AutoService(IRDPRApiPlatformHelper.class)
public class FabricRDPRApiPlatformHelper implements IRDPRApiPlatformHelper {
    @Override
    public <T> void fromExistingRegistry(IReloadableRegistryCreationHelper helper, ResourceKey<Registry<T>> registryKey) {
        Optional<RegistryDataLoader.RegistryData<?>> optionalRegistryData = DynamicRegistries.getDynamicRegistries().stream().filter(registryData -> registryData.key().location() == registryKey.location()).findFirst();
        if (optionalRegistryData.isEmpty()) {
            throw new NullPointerException("Tried making " + registryKey + "' reloadable. Which is either not a datapack registry or has not been registered.");
        }
        Optional<Map.Entry<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>>> optionalNetworkData = RegistrySynchronization.NETWORKABLE_REGISTRIES.entrySet().stream().filter(registryData -> registryData.getKey().location() == registryKey.location()).findFirst();
        helper.registerNetworkableReloadableRegistry(registryKey, (Codec<T>) optionalRegistryData.get().elementCodec(), optionalNetworkData.map(resourceKeyNetworkedRegistryDataEntry -> (Codec<T>) resourceKeyNetworkedRegistryDataEntry.getValue().networkCodec()).orElse(null));
    }

    @Override
    public RegistryOps<JsonElement> getRegistryOps(RegistryOps.RegistryInfoLookup lookup) {
        return RegistryOps.create(JsonOps.INSTANCE, lookup);
    }

    @Override
    public <T> Decoder<Optional<T>> getDecoder(Decoder<T> original) {
        return original.map(Optional::of);
    }
}