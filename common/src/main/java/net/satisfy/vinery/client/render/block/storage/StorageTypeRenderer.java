package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;

public interface StorageTypeRenderer {
    /**
     * Called on the client tick/extract phase, with the block entity still available. Implementations that need
     * baked item models must prepare them here (see {@link ShelfRenderer}); the state carries a copy of the
     * inventory already.
     */
    default void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver, float partialTick) {
    }

    void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector);
}
