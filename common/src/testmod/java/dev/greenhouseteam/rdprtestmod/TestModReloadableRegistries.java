package dev.greenhouseteam.rdprtestmod;

import dev.greenhouseteam.rdprtestmod.record.BasicRecord;
import dev.greenhouseteam.rdprtestmod.record.Chocolate;
import dev.greenhouseteam.rdprtestmod.record.LogRecord;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TestModReloadableRegistries {
    public static final ResourceKey<Registry<BasicRecord>> BASIC_RECORD = ResourceKey.createRegistryKey(new ResourceLocation("rdprtestmod", "basic_record"));
    public static final ResourceKey<Registry<LogRecord>> LOG_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("rdprtestmod", "log"));
    public static final ResourceKey<Registry<Chocolate>> CHOCOLATE_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("rdprtestmod", "chocolate"));

    public TestModReloadableRegistries() {}

    public static void createContents(IReloadableRegistryCreationHelper helper) {
        // Register existing registries created in platform specific code as reloadable.
        helper.fromExistingRegistry(BASIC_RECORD);
        helper.fromExistingRegistry(LOG_REGISTRY);
        helper.fromExistingRegistry(CHOCOLATE_REGISTRY);
    }

}
