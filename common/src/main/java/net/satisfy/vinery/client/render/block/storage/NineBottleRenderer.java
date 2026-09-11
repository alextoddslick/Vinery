package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.WineBottleBlock;

public class NineBottleRenderer implements StorageTypeRenderer {
    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.translate(-0.13, 0.335, 0.125);
        poseStack.scale(0.9f, 0.9f, 0.9f);

        for (int i = 0; i < state.items.size(); i++) {
            ItemStack stack = state.items.get(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
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

            BlockState blockState = blockItem.getBlock().defaultBlockState();
            if (blockState.hasProperty(WineBottleBlock.FAKE_MODEL)) {
                blockState = blockState.setValue(WineBottleBlock.FAKE_MODEL, false);
            }

            collector.submitBlock(poseStack, blockState, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
