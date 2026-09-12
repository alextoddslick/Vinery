package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;

public class ShelfRenderer implements StorageTypeRenderer {
    @Override
    public void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver,
                        BlockModelResolver blockModelResolver, float partialTick) {
        NonNullList<ItemStack> items = state.items;
        ItemStackRenderState[] renderStates = new ItemStackRenderState[items.size()];
        int seed = HashCommon.long2int(entity.getBlockPos().asLong());

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
                itemModelResolver.updateForTopItem(itemStackRenderState, stack, ItemDisplayContext.GUI, entity.getLevel(), null, seed + i);
                renderStates[i] = itemStackRenderState;
            }
        }

        state.itemRenderStates = renderStates;
    }

    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.translate(-0.4, 0.5, 0.25);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        for (int i = 0; i < state.itemRenderStates.length; i++) {
            ItemStackRenderState itemStackRenderState = state.itemRenderStates[i];
            if (itemStackRenderState != null && !itemStackRenderState.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0f, 0f, 0.2f * i);
                poseStack.mulPose(Axis.YN.rotationDegrees(22.5f));
                itemStackRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }
}
