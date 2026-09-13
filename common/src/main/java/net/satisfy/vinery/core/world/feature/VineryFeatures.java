package net.satisfy.vinery.core.world.feature;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.satisfy.vinery.core.Vinery;

import java.util.function.Supplier;

public class VineryFeatures {

    private static final Registrar<MapCodec<? extends Feature>> FEATURE_TYPES = DeferredRegister.create(Vinery.MOD_ID, Registries.FEATURE_TYPE).getRegistrar();
    public static final RegistrySupplier<MapCodec<JungleGrapeFeature>> JUNGLE_GRAPE_FEATURE = register("jungle_grape_feature", () -> JungleGrapeFeature.CODEC);

    public static void init(){
    }

    private static <F extends Feature> RegistrySupplier<MapCodec<F>> register(String name, Supplier<MapCodec<F>> codec) {
        return FEATURE_TYPES.register(Vinery.identifier(name), codec);
    }

}
