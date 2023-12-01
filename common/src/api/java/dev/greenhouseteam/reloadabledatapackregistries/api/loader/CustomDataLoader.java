package dev.greenhouseteam.reloadabledatapackregistries.api.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;
import dev.greenhouseteam.reloadabledatapackregistries.api.platform.IRDRApiPlatformHelper;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.Reader;
import java.util.Map;
import java.util.Optional;

/**
 * An interface that allows you to override the default Minecraft logic for loading
 * data pack registries.
 */
@FunctionalInterface
public interface CustomDataLoader<T> {
    CustomDataLoader<?> NOTHING = (lookup, resourceManager, writableRegistry, resourceKeyExceptionMap) -> {};

    void load(RegistryOps.RegistryInfoLookup lookup, ResourceManager resourceManager, WritableRegistry<T> registry, Map<ResourceKey<T>, Exception> exceptionMap);

    /**
     * Creates a custom loader which loads data from the namespace of the registry.
     * This is basically RegistryDataLoader#loadRegistryContents (Mojmap)/RegistryLoader#load (Yarn),
     * but with an extra hook to place your own code.
     *
     * @param functionality A functional interface that can act upon the CustomDataLoader
     *                      values and also the returned value from this load.
     * @param codec         The codec used to load data for this registry.
     * @param stage         Whether the functionality runs before or after registration.
     * @return A CustomDataLoader that loads from the namespace of the registry.
     */
    static <T> CustomDataLoader<T> extraFunction(InnerFunctionality<T> functionality, Codec<T> codec, Stage stage) {
        return (lookup, resourceManager, registry, exceptionMap) -> {
            FileToIdConverter fileToIdConverter = FileToIdConverter.json(registry.key().location().getNamespace() + "/" + registry.key().location().getPath());
            RegistryOps<JsonElement> ops = IRDRApiPlatformHelper.INSTANCE.getRegistryOps(lookup);
            Decoder<Optional<T>> decoder = IRDRApiPlatformHelper.INSTANCE.getDecoder(codec);

            for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
                Resource resource = entry.getValue();
                ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), fileToIdConverter.fileToId(entry.getKey()));
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonelement = JsonParser.parseReader(reader);
                    DataResult<Optional<T>> dataResult = decoder.parse(ops, jsonelement);
                    Optional<T> t = dataResult.getOrThrow(false, s -> {});
                    if (stage == Stage.BEFORE)
                        functionality.load(lookup, resourceManager, registry, exceptionMap, resourceKey, t);
                    t.ifPresentOrElse(e -> registry.register(resourceKey, e, Lifecycle.stable()), () -> LogUtils.getLogger().debug("Skipping loading registry entry {} as it's conditions were not met", resource));
                    if (stage == Stage.AFTER)
                           functionality.load(lookup, resourceManager, registry, exceptionMap, resourceKey, t);
                } catch (Exception ex) {
                    exceptionMap.put(resourceKey, ex);
                }
            }
        };
    }

    enum Stage {
        BEFORE,
        AFTER
    }

    @FunctionalInterface
    interface InnerFunctionality<T> {
        void load(RegistryOps.RegistryInfoLookup lookup, ResourceManager resourceManager, WritableRegistry<T> writableRegistry, Map<ResourceKey<T>, Exception> exceptionMap, ResourceKey<T> tResourceKey, Optional<T> t);
    }
}
