package dev.greenhouseteam.rdrtestmod.record;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PrioritizedRecord<T>(int priority, T record) {
    public static <T> Codec<PrioritizedRecord<T>> createCodec(Codec<T> originalCodec) {
        if (!(originalCodec instanceof MapCodec.MapCodecCodec<T> mapCodecCodec)) {
            throw new RuntimeException("Tried creating PrioritizedRecord codec from non MapCodecCodec.");
        }
        return RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.optionalFieldOf("priority", 0).forGetter(PrioritizedRecord::priority),
                mapCodecCodec.codec().forGetter(PrioritizedRecord::record)
        ).apply(inst, PrioritizedRecord::new));
    }
}
