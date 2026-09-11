package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.vinery.core.block.StorageBlock;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class StorageBlockEntityRenderer implements BlockEntityRenderer<StorageBlockEntity, StorageRenderState> {
    private static final HashMap<Identifier, StorageTypeRenderer> STORAGE_TYPES = new HashMap<>();

    private final ItemModelResolver itemModelResolver;

    public static void registerStorageType(Identifier name, StorageTypeRenderer renderer) {
        STORAGE_TYPES.put(name, renderer);
    }

    public static StorageTypeRenderer getRendererForId(Identifier name) {
        return STORAGE_TYPES.get(name);
    }

    public StorageBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public StorageRenderState createRenderState() {
        return new StorageRenderState();
    }

    @Override
    public void extractRenderState(StorageBlockEntity entity, StorageRenderState state, float partialTick, Vec3 cameraPos,
                                   @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumblingOverlay);
        state.storageType = null;
        state.items = NonNullList.create();
        state.itemRenderStates = StorageRenderState.NO_ITEM_STATES;

        BlockState blockState = entity.getBlockState();
        if (!(blockState.getBlock() instanceof StorageBlock storageBlock) || !entity.hasLevel()) {
            return;
        }

        state.storageType = storageBlock.type();

        NonNullList<ItemStack> inventory = entity.getInventory();
        NonNullList<ItemStack> copy = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.size(); i++) {
            copy.set(i, inventory.get(i).copy());
        }
        state.items = copy;

        StorageTypeRenderer renderer = getRendererForId(state.storageType);
        if (renderer != null) {
            renderer.extract(entity, state, this.itemModelResolver, partialTick);
        }
    }

    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (state.storageType == null) {
            return;
        }
        StorageTypeRenderer renderer = getRendererForId(state.storageType);
        if (renderer == null) {
            return;
        }

        poseStack.pushPose();
        applyBlockAngle(poseStack, state.blockState, 180);
        renderer.submit(state, poseStack, collector);
        poseStack.popPose();
    }

    public static void applyBlockAngle(PoseStack matrices, BlockState state, float angleOffset) {
        float angle = state.getValue(StorageBlock.FACING).toYRot();
        matrices.translate(0.5, 0, 0.5);
        matrices.mulPose(Axis.YP.rotationDegrees(angleOffset - angle));
    }
}
