package dev.greenhouseteam.rdpr.api.platform;

import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IRDPRApiPlatformHelper {

    IRDPRApiPlatformHelper INSTANCE = ServiceUtil.load(IRDPRApiPlatformHelper.class);

    <T> void fromExistingRegistry(IReloadableRegistryCreationHelper helper, ResourceKey<Registry<T>> registryKey);

}
