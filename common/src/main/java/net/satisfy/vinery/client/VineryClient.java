package net.satisfy.vinery.client;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.satisfy.vinery.client.gui.ApplePressGui;
import net.satisfy.vinery.client.gui.FermentationBarrelGui;
import net.satisfy.vinery.client.model.*;
import net.satisfy.vinery.client.render.block.CompletionistBannerRenderer;
import net.satisfy.vinery.client.render.block.LatticeRenderer;
import net.satisfy.vinery.client.render.block.storage.*;
import net.satisfy.vinery.client.render.entity.ChairRenderer;
import net.satisfy.vinery.client.render.entity.MuleRenderer;
import net.satisfy.vinery.client.render.entity.WanderingWinemakerRenderer;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.registry.EntityTypeRegistry;
import net.satisfy.vinery.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.vinery.core.registry.StorageTypeRegistry;

import static net.satisfy.vinery.core.registry.ObjectRegistry.*;

@Environment(EnvType.CLIENT)
public class VineryClient {
    public static final ModelLayerLocation DARK_CHERRY_BOAT_LAYER = new ModelLayerLocation(Vinery.identifier("boat/dark_cherry"), "main");
    public static final ModelLayerLocation DARK_CHERRY_CHEST_BOAT_LAYER = new ModelLayerLocation(Vinery.identifier("chest_boat/dark_cherry"), "main");

    public static void onInitializeClient() {
        RenderTypeRegistry.register(ChunkSectionLayer.CUTOUT,
                RED_GRAPE_BUSH.get(), WHITE_GRAPE_BUSH.get(), DARK_CHERRY_DOOR.get(), FERMENTATION_BARREL.get(),
                MELLOHI_WINE.get(), CLARK_WINE.get(), BOLVAR_WINE.get(), CHERRY_WINE.get(),
                LILITU_WINE.get(), CHENET_WINE.get(), NOIR_WINE.get(), APPLE_CIDER.get(),
                APPLE_WINE.get(), SOLARIS_WINE.get(), JELLIE_WINE.get(), AEGIS_WINE.get(), KELP_CIDER.get(),
                SAVANNA_RED_GRAPE_BUSH.get(), SAVANNA_WHITE_GRAPE_BUSH.get(),
                CHORUS_WINE.get(), STAL_WINE.get(), MAGNETIC_WINE.get(), STRAD_WINE.get(), JUNGLE_WHITE_GRAPE_BUSH.get(),
                JUNGLE_RED_GRAPE_BUSH.get(), TAIGA_RED_GRAPE_BUSH.get(), TAIGA_WHITE_GRAPE_BUSH.get(),
                GRAPEVINE_STEM.get(), WINE_BOX.get(), DARK_CHERRY_WINE_RACK_MID.get(), DARK_CHERRY_WINE_RACK_BIG.get(),
                APPLE_PRESS.get(), GRASS_SLAB.get(), DARK_CHERRY_SAPLING.get(), APPLE_TREE_SAPLING.get(),
                STACKABLE_LOG.get(), APPLE_LEAVES.get(), POTTED_APPLE_TREE_SAPLING.get(), DARK_CHERRY_WINE_RACK_SMALL.get(),
                POTTED_DARK_CHERRY_TREE_SAPLING.get(), RED_WINE.get(), DARK_CHERRY_CHAIR.get(), CRISTEL_WINE.get(),
                VILLAGERS_FRIGHT.get(), EISWEIN.get(), CREEPERS_CRUSH.get(),
                GLOWING_WINE.get(), JO_SPECIAL_MIXTURE.get(), MEAD.get(), BOTTLE_MOJANG_NOIR.get(),
                DARK_CHERRY_TABLE.get(), OAK_WINE_RACK_MID.get(), DARK_OAK_WINE_RACK_MID.get(), BIRCH_WINE_RACK_MID.get(),
                SPRUCE_WINE_RACK_MID.get(), JUNGLE_WINE_RACK_MID.get(), MANGROVE_WINE_RACK_MID.get(), BAMBOO_WINE_RACK_MID.get(),
                ACACIA_WINE_RACK_MID.get(), OAK_LATTICE.get(), SPRUCE_LATTICE.get(),
                BIRCH_LATTICE.get(), DARK_OAK_LATTICE.get(), CHERRY_LATTICE.get(), BAMBOO_LATTICE.get(), ACACIA_LATTICE.get(), JUNGLE_LATTICE.get(),
                MANGROVE_LATTICE.get(), DARK_CHERRY_LATTICE.get(), CHERRY_WINE_RACK_MID.get()
        );

        RenderTypeRegistry.register(ChunkSectionLayer.TRANSLUCENT, WINDOW.get(), WINDOW_BLOCK.get());

        // Item tints are data driven since 1.21.4; the grass slab item now needs a "minecraft:grass" tint in
        // assets/vinery/items/grass_slab.json instead of a ColorHandlerRegistry.registerItemColors call.
        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                    if (world == null || pos == null) {
                        return -1;
                    }
                    return BiomeColors.getAverageGrassColor(world, pos);
                }, GRASS_SLAB.get()
        );
        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getAverageFoliageColor(world, pos);
        }, JUNGLE_RED_GRAPE_BUSH.get(), JUNGLE_WHITE_GRAPE_BUSH.get());

        registerStorageType();
        registerBlockEntityRenderer();
    }

    public static void preInitClient() {
        registerEntityModelLayer();
        registerEntityRenderers();
    }

    public static void registerStorageTypes(ResourceLocation location, StorageTypeRenderer renderer) {
        StorageBlockEntityRenderer.registerStorageType(location, renderer);
    }

    public static void registerStorageType() {
        registerStorageTypes(StorageTypeRegistry.BIG_BOTTLE, new BigBottleRenderer());
        registerStorageTypes(StorageTypeRegistry.FOUR_BOTTLE, new FourBottleRenderer());
        registerStorageTypes(StorageTypeRegistry.NINE_BOTTLE, new NineBottleRenderer());
        registerStorageTypes(StorageTypeRegistry.SHELF, new ShelfRenderer());
        registerStorageTypes(StorageTypeRegistry.WINE_BOX, new WineBoxRenderer());
        registerStorageTypes(StorageTypeRegistry.WINE_BOTTLE, new WineBottleRenderer());
    }

    /**
     * Only called from the Fabric entrypoint: on NeoForge {@code RegisterMenuScreensEvent} fires before
     * {@link #onInitializeClient()} would run, so {@code VineryClientNeoForge} registers the screens itself.
     */
    public static void registerScreenFactory() {
        MenuScreenRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.FERMENTATION_BARREL_GUI_HANDLER.get(), FermentationBarrelGui::new);
        MenuScreenRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.APPLE_PRESS_GUI_HANDLER.get(), ApplePressGui::new);
    }

    @SuppressWarnings("unchecked")
    public static void registerBlockEntityRenderer() {
        BlockEntityRendererRegistry.register(EntityTypeRegistry.VINERY_STANDARD.get(), CompletionistBannerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STORAGE_ENTITY.get(), StorageBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.LATTICE.get(), LatticeRenderer::new);
        // Vanilla SignRenderer/HangingSignRenderer iterate WoodType.values() and therefore already cover
        // vinery:dark_cherry. Their generic signature is fixed to SignBlockEntity, hence the unchecked casts.
        BlockEntityRendererRegistry.register((BlockEntityType<SignBlockEntity>) (BlockEntityType<?>) EntityTypeRegistry.MOD_SIGN.get(), SignRenderer::new);
        BlockEntityRendererRegistry.register((BlockEntityType<SignBlockEntity>) (BlockEntityType<?>) EntityTypeRegistry.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.register(MuleModel.LAYER_LOCATION, MuleModel::getTexturedModelData);
        EntityModelLayerRegistry.register(StrawHatModel.LAYER_LOCATION, StrawHatModel::createBodyLayer);
        EntityModelLayerRegistry.register(WinemakerChestplateModel.LAYER_LOCATION, WinemakerChestplateModel::createBodyLayer);
        EntityModelLayerRegistry.register(WinemakerLeggingsModel.LAYER_LOCATION, WinemakerLeggingsModel::createBodyLayer);
        EntityModelLayerRegistry.register(WinemakerBootsModel.LAYER_LOCATION, WinemakerBootsModel::createBodyLayer);
        EntityModelLayerRegistry.register(CompletionistBannerRenderer.LAYER_LOCATION, CompletionistBannerRenderer::createBodyLayer);
        EntityModelLayerRegistry.register(CompletionistBannerRenderer.FLAG_LAYER_LOCATION, CompletionistBannerRenderer::createFlagLayer);
        EntityModelLayerRegistry.register(LatticeRenderer.LAYER_LOCATION, LatticeRenderer::getTexturedModelData);
        EntityModelLayerRegistry.register(DARK_CHERRY_BOAT_LAYER, BoatModel::createBoatModel);
        EntityModelLayerRegistry.register(DARK_CHERRY_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
    }

    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypeRegistry.CHAIR, ChairRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.MULE, MuleRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.WANDERING_WINEMAKER, WanderingWinemakerRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.DARK_CHERRY_BOAT, context -> new BoatRenderer(context, DARK_CHERRY_BOAT_LAYER));
        EntityRendererRegistry.register(EntityTypeRegistry.DARK_CHERRY_CHEST_BOAT, context -> new BoatRenderer(context, DARK_CHERRY_CHEST_BOAT_LAYER));
    }
}
