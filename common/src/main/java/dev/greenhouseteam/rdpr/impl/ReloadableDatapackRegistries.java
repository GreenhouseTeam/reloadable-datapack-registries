package dev.greenhouseteam.rdpr.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import dev.greenhouseteam.rdpr.impl.platform.IRDPRPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReloadableDatapackRegistries {

	public static final String MOD_ID = "rdpr";

	protected static final Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.RegistryData<?>> RELOADABLE_REGISTRY_DATA = Collections.synchronizedMap(new LinkedHashMap<>());
	protected static final Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = Collections.synchronizedMap(new LinkedHashMap<>());

	private static boolean hasLoaded = false;

	public static void init() {
		if (hasLoaded) return;
		IRDPRPlatformHelper.INSTANCE.invokeEntrypoints();
		hasLoaded = true;
	}

	public static boolean isReloadableRegistry(ResourceKey<? extends Registry<?>> registryKey) {
		return RELOADABLE_REGISTRY_DATA.containsKey(registryKey);
	}

	public static boolean hasNetworkableRegistries() {
		return !NETWORKABLE_REGISTRIES.isEmpty();
	}

	public static List<RegistryDataLoader.RegistryData<?>> getAllRegistryData() {
		if (!hasLoaded) {
			throw new UnsupportedOperationException("Called ReloadableDatapackRegistries#getOrCreateAllRegistryData too early, please call it after registration.");
		}

		return ImmutableList.copyOf(RELOADABLE_REGISTRY_DATA.values());
	}

	public static boolean isNetworkable(ResourceKey<? extends Registry<?>> key) {
		return NETWORKABLE_REGISTRIES.containsKey(key);
	}

	public static <E> Codec<RegistryAccess> getOrCreateNetworkCodec() {
		Codec<ResourceKey<? extends Registry<E>>> resourceKeyCodec = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
		Codec<Registry<E>> registryCodec = resourceKeyCodec.partialDispatch("type", (registry) -> DataResult.success(registry.key()), ($$0x) -> getNetworkCodec($$0x).map(($$1x) -> RegistryCodecs.networkCodec($$0x, Lifecycle.experimental(), $$1x)));
		UnboundedMapCodec<? extends  ResourceKey<? extends Registry<E>>, ? extends Registry<E>> unboundedMapCodec = Codec.unboundedMap(resourceKeyCodec, registryCodec);
		return captureMap(unboundedMapCodec);
	}

	private static <K extends ResourceKey<? extends Registry<?>>, V extends Registry<?>> Codec<RegistryAccess> captureMap(UnboundedMapCodec<K, V> unboundedMapCodec) {
		return unboundedMapCodec.xmap(
				RegistryAccess.ImmutableRegistryAccess::new,
				(registryAccess) -> registryAccess.registries().filter(registryEntry -> NETWORKABLE_REGISTRIES.containsKey(registryEntry.key())).collect(ImmutableMap.toImmutableMap(registryEntry -> (K)registryEntry.key(), registryEntry -> (V)registryEntry.value()))
		);
	}

	public static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> registryKey) {
		return (DataResult)Optional.ofNullable(NETWORKABLE_REGISTRIES.get(registryKey)).map((data) -> data.networkCodec()).map(DataResult::success).orElseGet(() ->  DataResult.error(() -> "Unknown or not serializable registry: " + registryKey));
	}
}
