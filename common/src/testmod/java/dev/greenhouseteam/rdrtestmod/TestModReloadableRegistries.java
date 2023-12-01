package dev.greenhouseteam.rdrtestmod;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import dev.greenhouseteam.rdpr.api.platform.IRDPRApiPlatformHelper;
import dev.greenhouseteam.rdrtestmod.record.BasicRecord;
import dev.greenhouseteam.rdrtestmod.record.Chocolate;
import dev.greenhouseteam.rdrtestmod.record.LogRecord;
import dev.greenhouseteam.rdrtestmod.record.PrioritizedRecord;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.loader.CustomDataLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestModReloadableRegistries {
    public static final ResourceKey<Registry<BasicRecord>> BASIC_RECORD = ResourceKey.createRegistryKey(new ResourceLocation("rdrtestmod", "basic_record"));
    ;
    public static final ResourceKey<Registry<LogRecord>> LOG_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("rdrtestmod", "log"));
    public static final ResourceKey<Registry<Chocolate>> CHOCOLATE_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("rdrtestmod", "chocolate"));

    public static final Logger LOG = LoggerFactory.getLogger("RDR Test Mod");

    public TestModReloadableRegistries() {}

    public static void createContents(IReloadableRegistryCreationHelper helper) {
        helper.fromExistingRegistry(BASIC_RECORD);
        helper.fromExistingRegistry(LOG_REGISTRY);
        helper.fromExistingRegistry(CHOCOLATE_REGISTRY);

        // Set the data loaders to log out the result after loading.
        helper.setCustomDataLoader(BASIC_RECORD, prioritisedDataLoader(BasicRecord.CODEC));
        // For the log registry, we don't want to actually register the result.
        // But instead, we log the value that was supposed to be registered.
        helper.setCustomDataLoader(LOG_REGISTRY, logCustomDataLoader());
    }

    private static <T> CustomDataLoader<T> prioritisedDataLoader(Codec<T> codec) {
        return (lookup, resourceManager, registry, exceptionMap) -> {
            Map<ResourceLocation, Integer> priorityList = new HashMap<>();
            FileToIdConverter fileToIdConverter = FileToIdConverter.json(registry.key().location().getNamespace() + "/" + registry.key().location().getPath());
            RegistryOps<JsonElement> ops = IRDPRApiPlatformHelper.INSTANCE.getRegistryOps(lookup);
            Decoder<Optional<PrioritizedRecord<T>>> decoder = IRDPRApiPlatformHelper.INSTANCE.getDecoder(PrioritizedRecord.createCodec(codec));

            for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
                Resource resource = entry.getValue();
                ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), fileToIdConverter.fileToId(entry.getKey()));
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonelement = JsonParser.parseReader(reader);
                    DataResult<Optional<PrioritizedRecord<T>>> dataResult = decoder.parse(ops, jsonelement);
                    Optional<PrioritizedRecord<T>> t = dataResult.getOrThrow(false, s -> {
                    });
                    t.ifPresentOrElse(e -> {
                        if (e.priority() > priorityList.getOrDefault(resourceKey.location(), Integer.MIN_VALUE)) {
                            registry.register(resourceKey, e.record(), Lifecycle.stable());
                            int newPriority = e.priority();
                            priorityList.put(resourceKey.location(), newPriority);
                        }
                    }, () -> LogUtils.getLogger().debug("Skipping loading registry entry {} as it's conditions were not met", resource));
                } catch (Exception ex) {
                    exceptionMap.put(resourceKey, ex);
                }
            }
        };
    }

    private static CustomDataLoader<LogRecord> logCustomDataLoader() {
        return (lookup, resourceManager, registry, exceptionMap) -> {
            FileToIdConverter fileToIdConverter = FileToIdConverter.json(LOG_REGISTRY.location().getNamespace() + "/" + LOG_REGISTRY.location().getPath());
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookup);
            for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
                Resource resource = entry.getValue();
                ResourceKey<LogRecord> resourceKey = ResourceKey.create(registry.key(), fileToIdConverter.fileToId(entry.getKey()));
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonelement = JsonParser.parseReader(reader);
                    DataResult<LogRecord> dataResult = LogRecord.CODEC.parse(ops, jsonelement);
                    LogRecord logRecord = dataResult.getOrThrow(false, s -> {});
                    LOG.info("\033[30;107m" + resourceKey.location() + " has been loaded. --- It's extra message is {}.\033[39;49m", logRecord.extraLogMessage().orElse("... they forgot to put something here..."));
                } catch (Exception ex) {
                    exceptionMap.put(resourceKey, ex);
                }
            }
        };
    }
}
