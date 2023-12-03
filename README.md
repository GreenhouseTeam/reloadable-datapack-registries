![https://github.com/GreenhouseTeam/reloadable-datapack-registries/blob/1.20.2/neoforge/src/main/resources/rdpr.png]
# Reloadable Data Pack Registries (RDPR)

Reloadable Data Pack Registries is a library that allows modders to let their datapack/dynamic registries to reload with use of the /reload command.

# Basic API Guide

## 1. Get the project into your build script.

Please use Jar in Jar for this dependency as I probably won't be releasing it outside of GitHub and my maven.

```groovy
repositories {
    ...
    maven {
        url 'https://maven.merchantpug.net/releases/'
    }
}
dependencies {
    ...

    // Fabric/Quilt
    modCompileOnly("dev.greenhouseteam.rdpr:rdpr-fabric:${rdpr_version}:api")
    modRuntimeOnly(include("dev.greenhouseteam.rdpr:rdpr-fabric:${rdpr_version}"))
    
    // NeoForge
    compileOnly "dev.greenhouseteam.rdpr:rdpr-neoforge:${rdpr_version}:api"
    runtimeOnly(jarJar("dev.greenhouseteam.rdpr:rdpr-neoforge:${rdpr_version}")) {
        jarJar.ranged(it, "[${rdpr_version},)")
    }

    // Common API (Multiloader Template/VanillaGradle)
    compileOnly "dev.greenhouseteam.rdpr:rdpr-common-mojmap:${rdpr_version}:api"
    
    // Common API (Loom)
    modCompileOnly "dev.greenhouseteam.rdpr:rdpr-common-intermediary:${rdpr_version}:api"
}
```

## 2. Register your registry.

Register your data packable registry as you usually would on your modloader of choice. Look or ask elsewhere if you don't know how to do that as this is out of scope for a tutorial on how to use this mod specifically.

## 3. Work with the API!

How this works is a little different depending on the modloader you are targeting, Fabric/Quilt uses the `rdpr` entrypoint with a class that extends `ReloadableRegistryPlugin`, whereas NeoForge uses an event on the mod event bus.

All of these examples are in Mojmaps, as that's what I use, for a site that can help you convert anything you're unsure of to Yarn if you use those mappings, I would recommend [Linkie](https://linkie.shedaniel.dev/mappings).

### Fabric/Quilt
<details>

As stated above, you are supposed to create a class that extends `ReloadableRegistryPlugin`

Then inside the `createContents` method, you can call the `fromExistingRegistry` method using the IReloadableRegistryCreationHelper to get the values of the specific registry key as a reloadable registry.

```java
public class FabricExample extends ReloadableRegistryPlugin {
    
    public void createContents(IReloadableRegistryCreationHelper helper) {
        // Generally you'll have your ResourceKeys elsewhere, just hook up the same resourcekey that you used to register your datapack.
        helper.fromExistingRegistry(ExampleMod.BASIC_RECORD);
    }
}
```

`fabric.mod.json`
```json
{
  "entrypoints": {
    "rdpr": [
      "your.reloadable.registry.plugin.Here"
    ]
  }
}  
```

`quilt.mod.json`
```json
{
  "quilt_loader": {
    "entrypoints": {
      "rdpr": "your.reloadable.registry.plugin.Here"
    }
  }
}
```

</details>

### NeoForge
<details>

On NeoForge, you must subscribe to the ReloadableRegistryEvent on the mod event bus.

Then you can call the `fromExistingRegistry` method in the event to get the values of the specific registry key as a reloadable registry.

The below is just one way you can subscribe to a NeoForge event, feel free to look into and use your preferred method.
```java
public class NeoForgeExample {
    
    @Mod.EventBusSubscriber(modid = CommonExample.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void addReloadableRegistries(ReloadableRegistryEvent event) {
            // Generally you'll have your ResourceKeys elsewhere, just hook up the same resourcekey that you used to register your datapack.
            helper.fromExistingRegistry(ExampleMod.BASIC_RECORD);
        }
    }
}
```

</details>

### MultiLoader/Architectury
<details>

You can actually use common code for the entrypoint/event. You are able to plug in `IReloadableRegistryCreationHelper` to a common method for use with loader specific registration.

#### Common
```java
public class CommonExample {
    public static final String MOD_ID = "example";
    // Don't forget to register this as a data pack/dynamic registry with your loader's methods!
    // You'll need a codec as well, but for the sake of space and making this example easy to understand, a codec is not included here.
    public static final ResourceKey<Registry<BasicRecord>> BASIC_RECORD = ResourceKey.createRegistryKey(new ResourceLocation(MOD_ID, "basic_record"));
    
    /**
     * A method that can be called on Fabric/Quilt/NeoForge's specific registration point
     * that will act as if you just called it normally over there.
     * 
     * @param helper    The helper with methods for registering and modifying your data pack registry.
     */
    public static void createContents(IReloadableRegistryCreationHelper helper) {
        // Register existing registries created in platform specific code as reloadable.
        helper.fromExistingRegistry(BASIC_RECORD);
    }
}
```

#### Fabric/Quilt

Look at the Fabric section if you want more information on this.

```java
public class FabricExample extends ReloadableRegistryPlugin {
    public void createContents(IReloadableRegistryCreationHelper helper) {
        // Call the common method in here!
        CommonExample.createContents(helper);
    }
}
```

#### NeoForge

Look at the NeoForge section if you want more information on this.

```java
public class NeoForgeExample {
    
    @Mod.EventBusSubscriber(modid = CommonExample.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void addReloadableRegistries(ReloadableRegistryEvent event) {
            // Call the common method in here!
            TestModReloadableRegistries.createContents(event);
        }
    }
}
```

</details>

## 4. You should be good to go!

You should be good to go, but if you are caching anything anywhere, make sure to recache it upon reloading or unexpected results may happen.
I would also not recommend making actual worldgen related registries reloadable, as it could lead to very unexpected results.


# Backport?
I probably won't be backporting this mod because I would probably have to re-adjust to Forge's registry system on their end, however, the license is open for you to do so. So feel free.

# Why NeoForge instead of MinecraftForge?
NeoForge has made some huge steps in the right direction when it comes to their registry system. Being allowed to use more vanilla code has worked out extremely well for me when it comes to this.
On top of this, I am continuing forward developing for NeoForge instead of MinecraftForge, I'd wait for other devs to make the jump as NeoForge is more likely to be the main Forge based ModLoader for 1.20.2+.