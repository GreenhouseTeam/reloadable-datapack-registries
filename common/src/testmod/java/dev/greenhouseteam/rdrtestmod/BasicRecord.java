package dev.greenhouseteam.rdrtestmod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public record BasicRecord(String color, Holder<EntityType<?>> entityType) {
    public static final Codec<BasicRecord> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("color").forGetter(BasicRecord::color),
            BuiltInRegistries.ENTITY_TYPE.holderByNameCodec().fieldOf("entity_type").forGetter(BasicRecord::entityType)
    ).apply(inst, BasicRecord::new));

}
