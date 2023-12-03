package dev.greenhouseteam.rdpr.api.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;
import dev.greenhouseteam.rdpr.api.platform.IRDPRApiPlatformHelper;
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
    /**
     * A CustomDataLoader that returns nothing, use this when you want a
     * data packable registry but are loading elsewhere.
     */
    CustomDataLoader<?> NOTHING = (lookup, resourceManager, writableRegistry, resourceKeyExceptionMap) -> {};

    /**
     * .The load logic used in the registry instead of the default Minecraft logic.
     *
     * @param lookup            The lookup for getting values from the previous state of the registry.
     * @param resourceManager   The resource manager asasociated wtih the current loading of JSON. This
     *                          will let you load data from directories, etc.
     * @param registry          The registry that is currently being loaded, usually used
     *                          to register content.
     * @param exceptionMap      The exception map to place any exceptions that should just be
     *                          logged instead of throwing an exception.
     */
    void load(RegistryOps.RegistryInfoLookup lookup, ResourceManager resourceManager, WritableRegistry<T> registry, Map<ResourceKey<T>, Exception> exceptionMap);

    /**
     * Creates a custom loader which loads data from the associated {@link ResourceLocation} of the registry.
     * This is basically RegistryDataLoader#loadRegistryContents (Mojmap)/RegistryLoader#load (Yarn),
     * but with an extra hook to place your own code instead of the default register code.
     *
     * @param functionality A functional interface that can act upon the CustomDataLoader
     *                      values and also the returned value from this load.
     * @param codec         The codec used to load data for this registry.
     * @return A CustomDataLoader that loads from the namespace of the registry.
     */
    static <T> CustomDataLoader<T> loadFromRegistryId(InnerFunctionality<T> functionality, Codec<T> codec) {
        return (lookup, resourceManager, registry, exceptionMap) -> {
            FileToIdConverter fileToIdConverter = FileToIdConverter.json(registry.key().location().getNamespace() + "/" + registry.key().location().getPath());
            RegistryOps<JsonElement> ops = IRDPRApiPlatformHelper.INSTANCE.getRegistryOps(lookup);
            Decoder<Optional<T>> decoder = IRDPRApiPlatformHelper.INSTANCE.getDecoder(codec);

            for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
                Resource resource = entry.getValue();
                ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), fileToIdConverter.fileToId(entry.getKey()));
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonelement = JsonParser.parseReader(reader);
                    DataResult<Optional<T>> dataResult = decoder.parse(ops, jsonelement);
                    Optional<T> t = dataResult.getOrThrow(false, s -> {});
                    functionality.load(lookup, resourceManager, registry, exceptionMap, resourceKey, t);
                } catch (Exception ex) {
                    exceptionMap.put(resourceKey, ex);
                }
            }
        };
    }

    @FunctionalInterface
    interface InnerFunctionality<T> {
        /**
         * Load logic with extra parameters for loading after a value has been created.
         *
         * @param lookup            The lookup for getting values from the previous state of the registry.
         * @param resourceManager   The resource manager asasociated wtih the current loading of JSON. This
         *                          will let you load data from directories, etc.
         * @param registry          The registry that is currently being loaded, usually used
         *                          to register content.
         * @param exceptionMap      The exception map to place any exceptions that should just be
         *                          logged instead of throwing an exception.
         * @param tResourceKey      The resource key of the value that would've been registered.
         * @param t                 An optional value of the value that was created from the Codec that created it.
         *                          Returns empty if NeoForge conditions return it as false.
         */
        void load(RegistryOps.RegistryInfoLookup lookup, ResourceManager resourceManager, WritableRegistry<T> registry, Map<ResourceKey<T>, Exception> exceptionMap, ResourceKey<T> tResourceKey, Optional<T> t);
    }
}
