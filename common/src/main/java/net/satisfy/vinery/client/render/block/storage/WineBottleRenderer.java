package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.WineBottleBlock;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;
import net.satisfy.vinery.core.item.DrinkBlockItem;
import net.satisfy.vinery.core.registry.ObjectRegistry;

@Environment(EnvType.CLIENT)
public class WineBottleRenderer implements StorageTypeRenderer {
    private static final int MAX_BOTTLES = 3;

    @Override
    public void extract(StorageBlockEntity entity, StorageRenderState state, ItemModelResolver itemModelResolver,
                        BlockModelResolver blockModelResolver, float partialTick) {
        for (int i = 0; i < MAX_BOTTLES; i++) {
            DrinkBlockItem item = drinkAt(state.items, i);
            if (item != null) {
                StorageTypeRenderer.resolveBlock(blockModelResolver, state, i, getState(item));
            }
        }
    }

    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        NonNullList<ItemStack> items = state.items;
        poseStack.translate(-0.5, 0, -0.5);
        switch (getCount(items)) {
            case 1 -> renderOne(state, poseStack, collector);
            case 2 -> renderTwo(state, poseStack, collector);
            case 3 -> renderThree(state, poseStack, collector, items);
        }
    }

    public int getCount(NonNullList<ItemStack> nonNullList) {
        int count = 0;
        for (ItemStack stack : nonNullList) {
            if (!stack.isEmpty()) count++;
        }
        return count;
    }

    private static DrinkBlockItem drinkAt(NonNullList<ItemStack> list, int index) {
        if (index >= list.size()) return null;
        return list.get(index).getItem() instanceof DrinkBlockItem item ? item : null;
    }

    private void renderOne(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        StorageTypeRenderer.submitBlock(state, 0, poseStack, collector);
    }

    private static BlockState getState(DrinkBlockItem item) {
        BlockState state = item.getBlock().defaultBlockState();
        if (state.hasProperty(WineBottleBlock.FAKE_MODEL)) {
            state = state.setValue(WineBottleBlock.FAKE_MODEL, false);
        }
        return state;
    }

    private void renderTwo(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.translate(-0.15f, 0f, -0.25f);
        StorageTypeRenderer.submitBlock(state, 0, poseStack, collector);
        poseStack.translate(.1f, 0f, .8f);
        poseStack.rotate(Axis.YP.rotationDegrees(30));
        StorageTypeRenderer.submitBlock(state, 1, poseStack, collector);
    }

    private void renderThree(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector, NonNullList<ItemStack> list) {
        DrinkBlockItem item3 = drinkAt(list, 2);
        poseStack.translate(-0.25f, 0f, -0.25f);
        StorageTypeRenderer.submitBlock(state, 0, poseStack, collector);
        poseStack.translate(.15f, 0f, .5f);
        StorageTypeRenderer.submitBlock(state, 1, poseStack, collector);
        if (item3 == null) return;
        if (item3.asItem().equals(ObjectRegistry.KELP_CIDER.get().asItem())) {
            poseStack.translate(.35f, .7f, -.13f);
            poseStack.rotate(Axis.XP.rotationDegrees(90));
            StorageTypeRenderer.submitBlock(state, 2, poseStack, collector);
            return;
        }
        poseStack.translate(.1f, 0f, 0f);
        poseStack.rotate(Axis.YP.rotationDegrees(30));
        StorageTypeRenderer.submitBlock(state, 2, poseStack, collector);
    }
}
