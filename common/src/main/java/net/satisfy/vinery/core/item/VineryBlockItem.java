package net.satisfy.vinery.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.satisfy.vinery.core.block.BlockTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * BlockItem that forwards tooltips to blocks implementing {@link BlockTooltip}
 * (Block#appendHoverText was removed in 1.21.10).
 */
public class VineryBlockItem extends BlockItem {
    public VineryBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        if (this.getBlock() instanceof BlockTooltip blockTooltip) {
            blockTooltip.appendBlockHoverText(stack, context, tooltip, flag);
        }
    }
}
