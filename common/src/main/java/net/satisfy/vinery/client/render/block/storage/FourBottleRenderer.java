package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;

public class FourBottleRenderer implements StorageTypeRenderer {
    @Override
    public void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver,
                        BlockModelResolver blockModelResolver, float partialTick) {
        for (int i = 0; i < state.items.size() && i < 4; i++) {
            StorageTypeRenderer.resolveBlock(blockModelResolver, state, i, StorageTypeRenderer.storedBlockState(state.items.get(i)));
        }
    }

    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.translate(-0.13, 0.335, 0.125);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        for (int i = 0; i < state.items.size() && i < 4; i++) {
            if (!StorageTypeRenderer.hasBlock(state, i)) {
                continue;
            }
            poseStack.pushPose();
            if (i == 0) {
                poseStack.translate(-0.35f, 0, 0f);
            } else if (i == 1) {
                poseStack.translate(0, -0.33f, 0f);
            } else if (i == 2) {
                poseStack.translate(-0.7f, -0.33f, 0f);
            } else {
                poseStack.translate(-0.35f, -0.66f, 0f);
            }
            poseStack.rotate(Axis.XN.rotationDegrees(90));

            StorageTypeRenderer.submitBlock(state, i, poseStack, collector);
            poseStack.popPose();
        }
    }
}
