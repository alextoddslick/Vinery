package net.satisfy.vinery.core.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

/**
 * {@code Block#appendHoverText} was removed in 1.21.10 (tooltips are an item-only concept now).
 * Blocks that used to add tooltip lines implement this instead; the mod's {@code BlockItem}
 * forwards {@code Item#appendHoverText} to {@link #appendBlockHoverText}.
 */
public interface BlockTooltip {
    void appendBlockHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, Consumer<Component> tooltip, TooltipFlag tooltipFlag);
}
