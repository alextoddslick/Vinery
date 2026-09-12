package net.satisfy.vinery.core.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class BasketItem extends BlockItem {
    public BasketItem(Block block, Properties settings) {
        super(block, settings.stacksTo(1));
    }

    private static List<ItemStackTemplate> getContents(ItemStack itemStack) {
        ItemContainerContents contents = itemStack.get(DataComponents.CONTAINER);
        return contents == null ? List.of() : ImmutableList.copyOf(contents.nonEmptyItems());
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return Optional.of(new BundleTooltip(new BundleContents(getContents(itemStack))));
    }
}
