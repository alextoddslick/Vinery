package net.satisfy.vinery.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.client.model.CompletionistBannerFlagModel;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.block.CompletionistBannerBlock;
import net.satisfy.vinery.core.block.CompletionistWallBannerBlock;
import net.satisfy.vinery.core.block.entity.CompletionistBannerEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CompletionistBannerRenderer implements BlockEntityRenderer<CompletionistBannerEntity, CompletionistBannerRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Vinery.MOD_ID, "banner"), "main");
    public static final ModelLayerLocation FLAG_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Vinery.MOD_ID, "banner"), "flag");

    public static final String FLAG = CompletionistBannerFlagModel.FLAG;
    private static final String POLE = "pole";
    private static final String BAR = "bar";

    /**
     * Pole and crossbar reuse the vanilla banner_base sprite off the banner atlas, exactly like the vanilla
     * BannerRenderer does since 26.1. The old ModelBakery.BANNER_BASE Material / MaterialSet pair is gone.
     */
    private static final SpriteId BASE_SPRITE = Sheets.BANNER_BASE;

    private final SpriteGetter sprites;
    private final CompletionistBannerFlagModel flagModel;
    private final ModelPart pole;
    private final ModelPart bar;

    public CompletionistBannerRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        ModelPart modelPart = context.bakeLayer(LAYER_LOCATION);
        this.pole = modelPart.getChild(POLE);
        this.bar = modelPart.getChild(BAR);
        this.flagModel = new CompletionistBannerFlagModel(context.bakeLayer(FLAG_LAYER_LOCATION));
    }

    /**
     * Pole and bar. The flag lives in its own layer ({@link #createFlagLayer()}) so that its wave animation can be
     * applied at render time.
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild(POLE, CubeListBuilder.create().texOffs(44, 0).addBox(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f), PartPose.ZERO);
        partDefinition.addOrReplaceChild(BAR, CubeListBuilder.create().texOffs(0, 42).addBox(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f), PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public static LayerDefinition createFlagLayer() {
        return CompletionistBannerFlagModel.createFlagLayer();
    }

    @Override
    public CompletionistBannerRenderState createRenderState() {
        return new CompletionistBannerRenderState();
    }

    @Override
    public void extractRenderState(CompletionistBannerEntity banner, CompletionistBannerRenderState state, float partialTick, Vec3 cameraPos,
                                   @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(banner, state, partialTick, cameraPos, crumblingOverlay);

        BlockState blockState = banner.getBlockState();
        state.texture = blockState.getBlock() instanceof CompletionistBannerBlock bannerBlock ? bannerBlock.getRenderTexture() : null;

        long time;
        if (banner.getLevel() == null) {
            time = 0L;
            state.standing = true;
            state.angle = 0.0F;
        } else {
            time = banner.getLevel().getGameTime();
            if (!(blockState.getBlock() instanceof CompletionistWallBannerBlock)) {
                state.standing = true;
                state.angle = (float) (-blockState.getValue(CompletionistBannerBlock.ROTATION) * 360) / 16.0F;
            } else {
                state.standing = false;
                state.angle = -blockState.getValue(CompletionistWallBannerBlock.FACING).toYRot() + 180.0F;
            }
        }

        BlockPos blockPos = banner.getBlockPos();
        state.phase = ((float) Math.floorMod(blockPos.getX() * 7L + blockPos.getY() * 9L + blockPos.getZ() * 13L + time, 100L) + partialTick) / 100.0F;
    }

    @Override
    public void submit(CompletionistBannerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (state.texture == null) {
            return;
        }

        float scale = 0.66f;
        poseStack.pushPose();
        if (state.standing) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(state.angle));
        } else {
            poseStack.translate(0.5, -0.1666666716337204, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(state.angle));
            poseStack.translate(0.0, -0.3125, -0.4375);
        }

        poseStack.pushPose();
        poseStack.scale(scale, -scale, -scale);

        RenderType baseType = BASE_SPRITE.renderType(RenderTypes::entitySolid);
        if (state.standing) {
            collector.submitModelPart(this.pole, poseStack, baseType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.sprites.get(BASE_SPRITE));
        }
        collector.submitModelPart(this.bar, poseStack, baseType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.sprites.get(BASE_SPRITE));

        collector.submitModel(this.flagModel, state.phase, poseStack, RenderTypes.entitySolid(state.texture),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);

        poseStack.popPose();
        poseStack.popPose();
    }
}
