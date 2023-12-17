package dev.greenhouseteam.rdpr.impl.platform;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.platform.IRDPRApiPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistriesHooks;

import java.util.Map;
import java.util.Optional;

@AutoService(IRDPRApiPlatformHelper.class)
public class NeoRDPRApiPlatformHelper implements IRDPRApiPlatformHelper {
    @Override
    public <T> void fromExistingRegistry(IReloadableRegistryCreationHelper helper, ResourceKey<Registry<T>> registryKey) {
        Optional<RegistryDataLoader.RegistryData<?>> optionalRegistryData = DataPackRegistriesHooks.getDataPackRegistries().stream().filter(registryData -> registryData.key().location() == registryKey.location()).findFirst();
        if (optionalRegistryData.isEmpty()) {
            throw new NullPointerException("Tried making " + registryKey + "' reloadable. Which is either not a datapack registry or has not been registered.");
        }
        Optional<Map.Entry<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>>> optionalNetworkData = RegistrySynchronization.NETWORKABLE_REGISTRIES.entrySet().stream().filter(registryData -> registryData.getKey().location() == registryKey.location()).findFirst();
        helper.registerNetworkableReloadableRegistry(registryKey, (Codec<T>) optionalRegistryData.get().elementCodec(), optionalNetworkData.map(resourceKeyNetworkedRegistryDataEntry -> (Codec<T>) resourceKeyNetworkedRegistryDataEntry.getValue().networkCodec()).orElse(null));
    }

}
