package dev.greenhouseteam.reloadabledatapackregistries;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import dev.greenhouseteam.reloadabledatapackregistries.access.CustomDataLoaderAccess;
import dev.greenhouseteam.reloadabledatapackregistries.platform.services.IRDRPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: Document all of this, potentially separate into API/Impl.
public class ReloadableDatapackRegistries {

	public static final String MOD_ID = "reloadabledatapackregistries";
	public static final String MOD_NAME = "Reloadable Datapack Registries";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	private static final Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.RegistryData<?>> RELOADABLE_REGISTRY_DATA = new LinkedHashMap<>();

	private static final Map<ResourceKey<? extends Registry<?>>, Codec<?>> NETWORK_CODECS = new LinkedHashMap<>();


	public static <T> void setupReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
		setupReloadableRegistry(registryKey, codec, codec);
	}

	public static <T> void setupReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networKCodec) {
		RegistryDataLoader.RegistryData<T> registryData = new RegistryDataLoader.RegistryData<>(registryKey, codec);
		RELOADABLE_REGISTRY_DATA.put(registryKey, registryData);
		if (networKCodec != null) {
			NETWORK_CODECS.put(registryKey, networKCodec);
		}
	}

	public static <T> void setCustomDataLoader(ResourceKey<Registry<T>> registryKey, CustomDataLoader<T> loader) {
		((CustomDataLoaderAccess)(Object)RELOADABLE_REGISTRY_DATA.get(registryKey)).reloadabledatapackregistries$setLoadFunction(Optional.of(loader));
	}

	public static boolean isReloadableRegistry(ResourceKey<? extends Registry<?>> registryKey) {
		return RELOADABLE_REGISTRY_DATA.containsKey(registryKey);
	}

	public static List<RegistryDataLoader.RegistryData<?>> getAllReloadableRegistryData() {
		return RELOADABLE_REGISTRY_DATA.values().stream().toList();
	}

	public static void populateWithNetworkCodecs(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder) {
		for (Map.Entry<ResourceKey<? extends Registry<?>>, Codec<?>> entry : NETWORK_CODECS.entrySet()) {
			builder.put(entry.getKey(), new RegistrySynchronization.NetworkedRegistryData(entry.getKey(), entry.getValue()));
		}
	}

}
