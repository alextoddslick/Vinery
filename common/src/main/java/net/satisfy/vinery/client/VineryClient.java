package net.satisfy.vinery.client;

import dev.architectury.registry.client.gui.MenuScreenRegistry;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.Identifier;
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
        // Block render layers are derived from the model/texture since 26.1: cutout is automatic and translucency is
        // declared per texture with {"force_translucent": true, "sprite": ...} in the block model. There is no
        // ItemBlockRenderTypes / RenderTypeRegistry / BlockRenderLayerMap any more, so nothing is registered here.
        // vinery:window and vinery:window_block were the only TRANSLUCENT blocks and need force_translucent models.

        // Item tints are data driven since 1.21.4; the grass slab item now needs a "minecraft:grass" tint in
        // assets/vinery/items/grass_slab.json instead of a ColorHandlerRegistry.registerItemColors call.
        ColorHandlerRegistry.registerBlockColors(BlockTintSources.grass(), GRASS_SLAB.get());
        ColorHandlerRegistry.registerBlockColors(BlockTintSources.foliage(),
                JUNGLE_RED_GRAPE_BUSH.get(), JUNGLE_WHITE_GRAPE_BUSH.get());

        registerStorageType();
        registerBlockEntityRenderer();
    }

    public static void preInitClient() {
        registerEntityModelLayer();
        registerEntityRenderers();
    }

    public static void registerStorageTypes(Identifier location, StorageTypeRenderer renderer) {
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
        // Vanilla StandingSignRenderer/HangingSignRenderer read Sheets.SIGN_SPRITES, which is built from
        // WoodType.values() and therefore already covers vinery:dark_cherry. Their generic signature is fixed to
        // SignBlockEntity, hence the unchecked casts.
        BlockEntityRendererRegistry.register((BlockEntityType<SignBlockEntity>) (BlockEntityType<?>) EntityTypeRegistry.MOD_SIGN.get(), StandingSignRenderer::new);
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
