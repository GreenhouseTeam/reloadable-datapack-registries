package dev.greenhouseteam.reloadabledatapackregistries.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.reloadabledatapackregistries.api.entrypoint.ReloadableRegistryEntrypoint;
import dev.greenhouseteam.reloadabledatapackregistries.api.entrypoint.ReloadableRegistryPlugin;
import dev.greenhouseteam.reloadabledatapackregistries.impl.ReloadableDatapackRegistries;
import dev.greenhouseteam.reloadabledatapackregistries.impl.ReloadableRegistryCreationHelper;
import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadRegistriesClientboundPacket;
import dev.greenhouseteam.reloadabledatapackregistries.network.ReloadableDatapackRegistriesNetworkHandler;
import dev.greenhouseteam.reloadabledatapackregistries.platform.services.IRDRPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.List;

@AutoService(IRDRPlatformHelper.class)
public class NeoRDRPlatformHelper implements IRDRPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void invokeEntrypoints() {
        Type type = Type.getType(ReloadableRegistryEntrypoint.class);
        for (ModFileScanData scanData : ModList.get().getAllScanData()) {
            List<ModFileScanData.AnnotationData> targets = scanData.getAnnotations().stream().
                    filter(annotationData -> type.equals(annotationData.annotationType())).
                    toList();
            for (ModFileScanData.AnnotationData target : targets) {
                try {
                    if (target.annotationType().equals(type)) {
                        Class<?> c = Class.forName(target.memberName());
                        if (!ReloadableRegistryPlugin.class.isAssignableFrom(c)) {
                            throw new UnsupportedOperationException("Attempted to load class '" + target.memberName()  + "' that does not extend ReloadableRegistryPlugin whislt loading ReloadableRegistryEntrypoints.");
                        }
                        ReloadableRegistryPlugin plugin = (ReloadableRegistryPlugin) c.newInstance();
                        plugin.createContents(ReloadableRegistryCreationHelper.INSTANCE);
                    } else
                        throw new UnsupportedOperationException("Attempted to non ReloadableRegistryEntrypoint whilst loading entrypoints of this type.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void sendReloadPacket(ReloadRegistriesClientboundPacket packet, List<ServerPlayer> serverPlayers) {
        ReloadableDatapackRegistriesNetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

}