package net.satisfy.vinery.core.world.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.satisfy.vinery.core.Vinery;

public class VineryConfiguredFeatures {

    public static final ResourceKey<Feature> DARK_CHERRY_KEY = registerKey("dark_cherry");
    public static final ResourceKey<Feature> DARK_CHERRY_VARIANT_KEY = registerKey("dark_cherry_variant");
    public static final ResourceKey<Feature> APPLE_KEY = registerKey("apple");
    public static final ResourceKey<Feature> APPLE_VARIANT_KEY = registerKey("apple_variant");

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Vinery.identifier(name));
    }

}

