package dev.greenhouseteam.rdrtestmod.record;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.rdrtestmod.TestModReloadableRegistriesPlugin;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Locale;
import java.util.Optional;

public record Chocolate(ChocolateType chocolateType, Optional<NutType> nutType) {
    public static final Codec<Chocolate> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.catchDecoderException(Codec.STRING.xmap(ChocolateType::byName, chocolateType -> chocolateType.name().toLowerCase(Locale.ROOT))).fieldOf("chocolate_type").forGetter(Chocolate::chocolateType),
            ExtraCodecs.catchDecoderException(Codec.STRING.xmap(NutType::byName, nutType -> nutType.name().toLowerCase(Locale.ROOT))).optionalFieldOf("nut_type").forGetter(Chocolate::nutType)
    ).apply(inst, Chocolate::new));

    public static final Codec<Holder<Chocolate>> CODEC = RegistryFileCodec.create(TestModReloadableRegistriesPlugin.CHOCOLATE_REGISTRY, DIRECT_CODEC);

    public enum ChocolateType {
        MILK,
        DARK,
        WHITE;

        public static ChocolateType byName(String name) {
            String lowerCaseName = name.toLowerCase(Locale.ROOT);
            for (ChocolateType type : ChocolateType.values()) {
                if (type.name().toLowerCase(Locale.ROOT).equals(lowerCaseName))
                    return type;
            }
            throw new UnsupportedOperationException("Could not find chocolate type from name " + name);
        }
    }

    public enum NutType {
        ALMOND("almonds"),
        CASHEW("cashews"),
        MACADAMIA("macadamias"),
        PEANUT("peanuts"),
        PISTACHIO("pistachios"),
        WALNUT("walnuts");

        private final String plural;

        NutType(String plural) {
            this.plural = plural;
        }

        public String plural() {
            return this.plural;
        }

        public static NutType byName(String name) {
            String lowerCaseName = name.toLowerCase(Locale.ROOT);
            for (NutType type : NutType.values()) {
                if (type.name().toLowerCase(Locale.ROOT).equals(lowerCaseName) || type.plural.equals(lowerCaseName))
                    return type;
            }
            throw new UnsupportedOperationException("Could not find nut type from name " + name);
        }
    }

}
