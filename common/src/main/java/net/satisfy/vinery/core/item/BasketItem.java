package net.satisfy.vinery.core.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class BasketItem extends BlockItem {
    public BasketItem(Block block, Properties settings) {
        super(block, settings.stacksTo(1));
    }

    private static Stream<ItemStack> getContents(ItemStack itemStack) {
        ItemContainerContents contents = itemStack.get(DataComponents.CONTAINER);
        return contents == null ? Stream.empty() : contents.nonEmptyStream();
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        Stream<ItemStack> stream = getContents(itemStack);
        Objects.requireNonNull(nonNullList);
        stream.forEach(nonNullList::add);
        return Optional.of(new BundleTooltip(new BundleContents(nonNullList)));
    }
}
