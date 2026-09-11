package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.WineBottleBlock;

public class BigBottleRenderer implements StorageTypeRenderer {
    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.items.isEmpty()) return;

        poseStack.translate(-0.4, 0.07, -0.5);
        poseStack.scale(0.8f, 0.8f, 0.9f);
        ItemStack stack = state.items.get(0);
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            BlockState blockState = blockItem.getBlock().defaultBlockState();
            if (blockState.hasProperty(WineBottleBlock.FAKE_MODEL)) {
                blockState = blockState.setValue(WineBottleBlock.FAKE_MODEL, false);
            }
            collector.submitBlock(poseStack, blockState, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
    }
}
