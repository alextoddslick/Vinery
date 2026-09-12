package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.WineBottleBlock;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;
import org.jetbrains.annotations.Nullable;

public interface StorageTypeRenderer {
    /** Display context shared by every Vinery storage block, mirroring the vanilla per-renderer constants. */
    BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    /**
     * Called on the client tick/extract phase, with the block entity still available. Implementations that need
     * baked item models must prepare them here (see {@link ShelfRenderer}); implementations that draw stored items
     * as block models must resolve those models here too, because {@code SubmitNodeCollector.submitBlock} is gone
     * since 26.1 and block models may only be looked up during extraction. The state carries a copy of the
     * inventory already.
     */
    default void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver,
                         BlockModelResolver blockModelResolver, float partialTick) {
    }

    void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector);

    /**
     * The block state a stored bottle-like item renders as, or {@code null} when the stack is not a block item.
     * {@code FAKE_MODEL} is cleared so the real bottle geometry is used instead of the flat inventory model.
     */
    @Nullable
    static BlockState storedBlockState(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }
        BlockState blockState = blockItem.getBlock().defaultBlockState();
        if (blockState.hasProperty(WineBottleBlock.FAKE_MODEL)) {
            blockState = blockState.setValue(WineBottleBlock.FAKE_MODEL, false);
        }
        return blockState;
    }

    /** Resolves {@code blockState} into the render state slot {@code index}; a no-op for an out-of-range index. */
    static void resolveBlock(BlockModelResolver resolver, StorageRenderState state, int index, @Nullable BlockState blockState) {
        if (blockState != null && index >= 0 && index < state.blockModels.length) {
            resolver.update(state.blockModels[index], blockState, BLOCK_DISPLAY_CONTEXT);
        }
    }

    /** Submits the block model resolved into slot {@code index}; a no-op when nothing was resolved there. */
    static void submitBlock(StorageRenderState state, int index, PoseStack poseStack, SubmitNodeCollector collector) {
        if (index >= 0 && index < state.blockModels.length) {
            state.blockModels[index].submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    static boolean hasBlock(StorageRenderState state, int index) {
        return index >= 0 && index < state.blockModels.length && !state.blockModels[index].isEmpty();
    }
}
