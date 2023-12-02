package dev.greenhouseteam.rdpr.impl.network;

import dev.greenhouseteam.rdpr.impl.ReloadableDatapackRegistries;
import dev.greenhouseteam.rdpr.mixin.LevelAccessor;
import dev.greenhouseteam.rdpr.mixin.client.ClientPacketListenerAccessor;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public record ReloadRegistriesClientboundPacket(RegistryAccess.Frozen registryHolder) {
    public static final ResourceLocation ID = new ResourceLocation(ReloadableDatapackRegistries.MOD_ID, "reload_registries");

    public FriendlyByteBuf toBuf() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encode(buf);
        return buf;
    }

    private static final RegistryOps<Tag> BUILTIN_CONTEXT_OPS = RegistryOps.create(
            NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    );

    public void encode(FriendlyByteBuf buf) {
        buf.writeWithCodec(BUILTIN_CONTEXT_OPS, ReloadableDatapackRegistries.getOrCreateNetworkCodec(), this.registryHolder());
    }

    public ResourceLocation getFabricId() {
        return ID;
    }

    public static ReloadRegistriesClientboundPacket decode(FriendlyByteBuf buf) {
        return new ReloadRegistriesClientboundPacket(buf.readWithCodecTrusted(BUILTIN_CONTEXT_OPS, ReloadableDatapackRegistries.getOrCreateNetworkCodec()).freeze());
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            RegistryAccess.Frozen frozen = new RegistryAccess.ImmutableRegistryAccess(connection.registryAccess().registries().filter(registryEntry -> !ReloadableDatapackRegistries.isNetworkable(registryEntry.key()))).freeze();
            RegistryAccess.Frozen newRegistryAccess = new RegistryAccess.ImmutableRegistryAccess(Stream.concat(frozen.registries(), registryHolder().registries())).freeze();
            newRegistryAccess.registries().forEach(p_296478_ -> p_296478_.value().resetTags());
            ((ClientPacketListenerAccessor)connection).rdpr$setRegistryAccess(newRegistryAccess);
            ((LevelAccessor)Minecraft.getInstance().level).rdpr$setRegistryAccess(newRegistryAccess);
        });
    }

}
