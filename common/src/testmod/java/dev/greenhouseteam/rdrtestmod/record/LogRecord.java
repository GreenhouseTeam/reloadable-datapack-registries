package dev.greenhouseteam.rdrtestmod.record;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record LogRecord(Optional<String> extraLogMessage) {
    public static final Codec<LogRecord> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.optionalFieldOf("extra_log_message").forGetter(LogRecord::extraLogMessage)
    ).apply(inst, LogRecord::new));

}
