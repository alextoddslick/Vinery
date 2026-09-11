package net.satisfy.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.WineBottleBlock;
import net.satisfy.vinery.core.item.DrinkBlockItem;
import net.satisfy.vinery.core.registry.ObjectRegistry;

@Environment(EnvType.CLIENT)
public class WineBottleRenderer implements StorageTypeRenderer {
    @Override
    public void submit(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        NonNullList<ItemStack> items = state.items;
        poseStack.translate(-0.5, 0, -0.5);
        switch (getCount(items)) {
            case 1 -> renderOne(state, poseStack, collector, items);
            case 2 -> renderTwo(state, poseStack, collector, items);
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

    private void submitBottle(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector, DrinkBlockItem item) {
        collector.submitBlock(poseStack, getState(item), state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }

    private void renderOne(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector, NonNullList<ItemStack> list) {
        DrinkBlockItem item = drinkAt(list, 0);
        if (item != null) {
            submitBottle(state, poseStack, collector, item);
        }
    }

    private static BlockState getState(DrinkBlockItem item) {
        BlockState state = item.getBlock().defaultBlockState();
        if (state.hasProperty(WineBottleBlock.FAKE_MODEL)) {
            state = state.setValue(WineBottleBlock.FAKE_MODEL, false);
        }
        return state;
    }

    private void renderTwo(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector, NonNullList<ItemStack> list) {
        DrinkBlockItem item1 = drinkAt(list, 0);
        DrinkBlockItem item2 = drinkAt(list, 1);

        poseStack.translate(-0.15f, 0f, -0.25f);
        if (item1 != null) {
            submitBottle(state, poseStack, collector, item1);
        }
        poseStack.translate(.1f, 0f, .8f);
        poseStack.mulPose(Axis.YP.rotationDegrees(30));
        if (item2 != null) {
            submitBottle(state, poseStack, collector, item2);
        }
    }

    private void renderThree(StorageRenderState state, PoseStack poseStack, SubmitNodeCollector collector, NonNullList<ItemStack> list) {
        DrinkBlockItem item1 = drinkAt(list, 0);
        DrinkBlockItem item2 = drinkAt(list, 1);
        DrinkBlockItem item3 = drinkAt(list, 2);
        poseStack.translate(-0.25f, 0f, -0.25f);
        if (item1 != null) {
            submitBottle(state, poseStack, collector, item1);
        }
        poseStack.translate(.15f, 0f, .5f);
        if (item2 != null) {
            submitBottle(state, poseStack, collector, item2);
        }
        if (item3 == null) return;
        if (item3.asItem().equals(ObjectRegistry.KELP_CIDER.get().asItem())) {
            poseStack.translate(.35f, .7f, -.13f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            submitBottle(state, poseStack, collector, item3);
            return;
        }
        poseStack.translate(.1f, 0f, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(30));
        submitBottle(state, poseStack, collector, item3);
    }
}
