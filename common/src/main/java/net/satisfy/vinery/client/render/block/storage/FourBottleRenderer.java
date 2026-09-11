package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.WineBottleBlock;

public class FourBottleRenderer implements StorageTypeRenderer {
    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.translate(-0.13, 0.335, 0.125);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        for (int i = 0; i < state.items.size(); i++) {
            ItemStack stack = state.items.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                poseStack.pushPose();
                if (i == 0) {
                    poseStack.translate(-0.35f, 0, 0f);
                } else if (i == 1) {
                    poseStack.translate(0, -0.33f, 0f);
                } else if (i == 2) {
                    poseStack.translate(-0.7f, -0.33f, 0f);
                } else if (i == 3) {
                    poseStack.translate(-0.35f, -0.66f, 0f);
                } else {
                    poseStack.popPose();
                    continue;
                }
                poseStack.mulPose(Axis.XN.rotationDegrees(90));

                BlockState blockState = blockItem.getBlock().defaultBlockState();
                if (blockState.hasProperty(WineBottleBlock.FAKE_MODEL)) {
                    blockState = blockState.setValue(WineBottleBlock.FAKE_MODEL, false);
                }
                collector.submitBlock(poseStack, blockState, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }
}
