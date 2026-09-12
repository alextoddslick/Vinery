package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;

public class NineBottleRenderer implements StorageTypeRenderer {
    @Override
    public void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver,
                        BlockModelResolver blockModelResolver, float partialTick) {
        for (int i = 0; i < state.items.size() && i < 9; i++) {
            StorageTypeRenderer.resolveBlock(blockModelResolver, state, i, StorageTypeRenderer.storedBlockState(state.items.get(i)));
        }
    }

    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.translate(-0.13, 0.335, 0.125);
        poseStack.scale(0.9f, 0.9f, 0.9f);

        for (int i = 0; i < state.items.size() && i < 9; i++) {
            if (!StorageTypeRenderer.hasBlock(state, i)) {
                continue;
            }

            poseStack.pushPose();

            int line = i >= 6 ? 3 : i >= 3 ? 2 : 1;
            float x;
            float y;

            if (line == 1) {
                x = -0.35f * i;
                y = 0f;
            } else if (line == 2) {
                x = -0.35f * (i - 3);
                y = -0.33f;
            } else {
                x = -0.35f * (i - 6);
                y = -0.66f;
            }

            poseStack.translate(x, y, 0f);
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));

            StorageTypeRenderer.submitBlock(state, i, poseStack, collector);
            poseStack.popPose();
        }
    }
}
