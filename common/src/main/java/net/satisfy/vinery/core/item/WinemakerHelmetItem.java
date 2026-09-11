package net.satisfy.vinery.core.item;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class WinemakerHelmetItem extends Item {
    private final ResourceLocation hatTexture;

    public WinemakerHelmetItem(ArmorMaterial armorMaterial, ArmorType type, Properties properties, ResourceLocation hatTexture) {
        super(properties.humanoidArmor(armorMaterial, type));
        this.hatTexture = hatTexture;
    }

    public ResourceLocation getHatTexture() {
        return hatTexture;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext ctx, @NotNull TooltipDisplay display, @NotNull Consumer<Component> consumer, @NotNull TooltipFlag flag) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.isClientSide()) {
            List<Component> tooltip = new ArrayList<>();
            ArmorRegistryClient.appendToolTip(tooltip);
            tooltip.forEach(consumer);
        }
    }
}
