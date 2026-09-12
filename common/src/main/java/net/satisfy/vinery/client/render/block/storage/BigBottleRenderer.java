package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;

public class BigBottleRenderer implements StorageTypeRenderer {
    @Override
    public void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver,
                        BlockModelResolver blockModelResolver, float partialTick) {
        if (state.items.isEmpty()) return;
        StorageTypeRenderer.resolveBlock(blockModelResolver, state, 0, StorageTypeRenderer.storedBlockState(state.items.get(0)));
    }

    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.items.isEmpty()) return;

        poseStack.translate(-0.4, 0.07, -0.5);
        poseStack.scale(0.8f, 0.8f, 0.9f);
        StorageTypeRenderer.submitBlock(state, 0, poseStack, collector);
    }
}
