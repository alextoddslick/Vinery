package net.satisfy.vinery.client.render.block.storage;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class StorageRenderState extends BlockEntityRenderState {
    public static final ItemStackRenderState[] NO_ITEM_STATES = new ItemStackRenderState[0];

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
}
