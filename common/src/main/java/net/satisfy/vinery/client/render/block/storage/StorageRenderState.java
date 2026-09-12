package net.satisfy.vinery.client.render.block.storage;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class StorageRenderState extends BlockEntityRenderState {
    public static final ItemStackRenderState[] NO_ITEM_STATES = new ItemStackRenderState[0];
    public static final BlockModelRenderState[] NO_BLOCK_MODELS = new BlockModelRenderState[0];

    /**
     * The storage type id of the {@link net.satisfy.vinery.core.block.StorageBlock} this state was extracted from,
     * or {@code null} when the block is not a storage block.
     */
    @Nullable
    public Identifier storageType;

    /**
     * A copy of the block entity inventory; safe to read on the render thread.
     */
    public NonNullList<ItemStack> items = NonNullList.create();

    /**
     * Only filled in by storage type renderers that render actual item models (the shelf).
     */
    public ItemStackRenderState[] itemRenderStates = NO_ITEM_STATES;

    /**
     * Block models of the stored items, one slot per inventory slot, filled in by the storage type renderers that
     * draw their contents as blocks. Resolving a block model needs the model manager and may therefore only happen
     * while extracting, which is why the models live on the render state instead of being looked up in
     * {@code submit}.
     */
    public BlockModelRenderState[] blockModels = NO_BLOCK_MODELS;

    /** Grows {@link #blockModels} to {@code size} entries (reusing what is there) and clears every entry. */
    public void resetBlockModels(int size) {
        if (this.blockModels.length != size) {
            BlockModelRenderState[] resized = new BlockModelRenderState[size];
            for (int i = 0; i < size; i++) {
                resized[i] = i < this.blockModels.length ? this.blockModels[i] : new BlockModelRenderState();
            }
            this.blockModels = resized;
        }
        for (BlockModelRenderState blockModel : this.blockModels) {
            blockModel.clear();
        }
    }
}
