package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;

public class WineBoxRenderer implements StorageTypeRenderer {
    @Override
    public void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver,
                        BlockModelResolver blockModelResolver, float partialTick) {
        if (state.items.isEmpty()) return;
        StorageTypeRenderer.resolveBlock(blockModelResolver, state, 0, StorageTypeRenderer.storedBlockState(state.items.get(0)));
    }

    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (!StorageTypeRenderer.hasBlock(state, 0)) return;

        poseStack.translate(0.35, 0.6, -0.35);
        poseStack.scale(0.7f, 0.7f, 0.7f);

        poseStack.rotate(Axis.ZP.rotationDegrees(90f));
        poseStack.rotate(Axis.YN.rotationDegrees(90f));

        StorageTypeRenderer.submitBlock(state, 0, poseStack, collector);
    }
}
