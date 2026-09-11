package net.satisfy.vinery.core.item;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.satisfy.vinery.client.util.ClientUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.satisfy.vinery.core.registry.ArmorRegistryClient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WinemakerChestItem extends Item {
    private final Identifier chestplateTexture;

    public WinemakerChestItem(ArmorMaterial armorMaterial, ArmorType type, Properties properties, Identifier chestplateTexture) {
        super(properties.humanoidArmor(armorMaterial, type));
        this.chestplateTexture = chestplateTexture;
    }

    public Identifier getChestplateTexture() {
        return chestplateTexture;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext ctx, @NotNull TooltipDisplay display, @NotNull Consumer<Component> consumer, @NotNull TooltipFlag flag) {
        if (Platform.getEnvironment() == Env.CLIENT && ClientUtil.hasClientLevel()) {
            List<Component> tooltip = new ArrayList<>();
            ArmorRegistryClient.appendToolTip(tooltip);
            tooltip.forEach(consumer);
        }
    }
}
