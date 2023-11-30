package dev.greenhouseteam.rdrtestmod;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.greenhouseteam.reloadabledatapackregistries.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.reloadabledatapackregistries.api.loader.CustomDataLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.Map;

public class RDRTestMod {
    public static final ResourceKey<Registry<BasicRecord>> BASIC_RECORD = ResourceKey.createRegistryKey(new ResourceLocation("rdrtestmod", "basic_record"));
    ;
    public static final ResourceKey<Registry<BasicRecord>> LOG_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("rdrtestmod", "log"));

    public static final Logger LOG = LoggerFactory.getLogger("RDR Test Mod");

    protected static CustomDataLoader<BasicRecord> logCustomDataLoader() {
        return (lookup, resourceManager, registry, exceptionMap) -> {
            FileToIdConverter fileToIdConverter = FileToIdConverter.json(LOG_REGISTRY.location().getNamespace() + "/" + LOG_REGISTRY.location().getPath());
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookup);
            for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
                Resource resource = entry.getValue();
                ResourceKey<BasicRecord> resourceKey = ResourceKey.create(registry.key(), fileToIdConverter.fileToId(entry.getKey()));
                try (Reader reader = resource.openAsReader()) {
                    try {
                        JsonElement jsonelement = JsonParser.parseReader(reader);
                        DataResult<BasicRecord> dataResult = BasicRecord.CODEC.parse(ops, jsonelement);
                        BasicRecord basicRecord = dataResult.getOrThrow(false, s -> {});
                        LOG.info("\033[30;107m" + resourceKey.location() + " has been loaded. --- The color is '{}' and the entity type is '{}'." + "\033[39;49m", basicRecord.color(), basicRecord.entityType().unwrapKey().isEmpty() ? null : basicRecord.entityType().unwrapKey().get().location());
                    } catch (Throwable throwable) {
                        try {
                            reader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                } catch (Exception ex) {
                    exceptionMap.put(resourceKey, ex);
                }
            }
        };
    }
}
