package net.satisfy.vinery.core;

import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.satisfy.vinery.core.command.WineDebugCommands;
import net.satisfy.vinery.core.event.EventHandler;
import net.satisfy.vinery.core.network.VineryNetwork;
import net.satisfy.vinery.core.registry.*;
import net.satisfy.vinery.core.util.WineEffectSetup;
import net.satisfy.vinery.core.world.feature.VineryFeatures;
import net.satisfy.vinery.platform.PlatformHelper;

public class Vinery {
    public static final String MOD_ID = "vinery";

    public static void init() {
        VineryNetwork.init();
        MobEffectRegistry.register();
        ObjectRegistry.init();
        EntityTypeRegistry.init();
        ScreenhandlerTypeRegistry.init();
        RecipeTypesRegistry.init();
        VineryFeatures.init();
        SoundEventRegistry.init();
        EventHandler.init();
        TabRegistry.init();
        WineDebugCommands.init();
        DataComponentRegistry.COMPONENTS.register();
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void commonSetup() {
        FlammableBlockRegistry.init();
        GrapeTypeRegistry.addGrapeAttributes();
        WineEffectSetup.setupWineEffects();
        FuelRegistry.register(1000, ObjectRegistry.DARK_CHERRY_FENCE.get(), ObjectRegistry.DARK_CHERRY_FENCE_GATE.get(), ObjectRegistry.STACKABLE_LOG.get(), ObjectRegistry.FERMENTATION_BARREL.get());

        // 26.3: AxeItem/ShovelItem are gone (Architectury's AxeItemHooks/ShovelItemHooks would crash at runtime);
        // stripping/flattening are block-transformer data, extended per platform (Fabric: BlockTransformerHelper).
        PlatformHelper.registerStrippable(ObjectRegistry.DARK_CHERRY_LOG.get(), ObjectRegistry.STRIPPED_DARK_CHERRY_LOG.get());
        PlatformHelper.registerStrippable(ObjectRegistry.DARK_CHERRY_WOOD.get(), ObjectRegistry.STRIPPED_DARK_CHERRY_WOOD.get());
        PlatformHelper.registerStrippable(ObjectRegistry.APPLE_LOG.get(), Blocks.STRIPPED_OAK_LOG);
        PlatformHelper.registerStrippable(ObjectRegistry.APPLE_WOOD.get(), Blocks.STRIPPED_OAK_WOOD);

        PlatformHelper.registerFlattenable(ObjectRegistry.GRASS_SLAB.get(), Blocks.DIRT_PATH.defaultBlockState());
    }
}
