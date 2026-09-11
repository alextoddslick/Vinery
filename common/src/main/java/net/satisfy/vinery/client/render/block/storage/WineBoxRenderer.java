package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.WineBottleBlock;

public class WineBoxRenderer implements StorageTypeRenderer {
    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.items.isEmpty()) return;

        poseStack.translate(0.35, 0.6, -0.35);
        poseStack.scale(0.7f, 0.7f, 0.7f);

        ItemStack stack = state.items.get(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
        poseStack.mulPose(Axis.YN.rotationDegrees(90f));

        BlockState renderState = blockItem.getBlock().defaultBlockState();
        if (renderState.hasProperty(WineBottleBlock.FAKE_MODEL)) {
            renderState = renderState.setValue(WineBottleBlock.FAKE_MODEL, false);
        }

        collector.submitBlock(poseStack, renderState, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }
}
