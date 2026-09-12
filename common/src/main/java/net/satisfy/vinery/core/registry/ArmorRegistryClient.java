package net.satisfy.vinery.core.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.vinery.client.model.StrawHatModel;
import net.satisfy.vinery.client.model.WinemakerBootsModel;
import net.satisfy.vinery.client.model.WinemakerChestplateModel;
import net.satisfy.vinery.client.model.WinemakerLeggingsModel;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.item.WinemakerBootsItem;
import net.satisfy.vinery.core.item.WinemakerChestItem;
import net.satisfy.vinery.core.item.WinemakerHelmetItem;
import net.satisfy.vinery.core.item.WinemakerLegsItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ArmorRegistryClient {
    /**
     * Texture of the {@code vinery:winemaker} equipment asset. All four pieces share it, exactly as they did in
     * 1.21.1 where they all pointed at {@code vinery:textures/models/armor/winemaker.png}.
     */
    public static final Identifier ARMOR_TEXTURE = Vinery.identifier("textures/entity/equipment/humanoid/winemaker.png");
    public static final Identifier LEGGINGS_TEXTURE = Vinery.identifier("textures/entity/equipment/humanoid_leggings/winemaker.png");

    private static EntityModelSet cachedModelSet;
    private static StrawHatModel hatModel;
    private static WinemakerChestplateModel chestplateModel;
    private static WinemakerLeggingsModel leggingsModel;
    private static WinemakerBootsModel bootsModel;

    private static void bakeIfNeeded() {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        if (modelSet == cachedModelSet) {
            return;
        }
        cachedModelSet = modelSet;
        hatModel = new StrawHatModel(modelSet.bakeLayer(StrawHatModel.LAYER_LOCATION));
        chestplateModel = new WinemakerChestplateModel(modelSet.bakeLayer(WinemakerChestplateModel.LAYER_LOCATION));
        leggingsModel = new WinemakerLeggingsModel(modelSet.bakeLayer(WinemakerLeggingsModel.LAYER_LOCATION));
        bootsModel = new WinemakerBootsModel(modelSet.bakeLayer(WinemakerBootsModel.LAYER_LOCATION));
    }

    public static StrawHatModel getHatModel() {
        bakeIfNeeded();
        return hatModel;
    }

    public static WinemakerChestplateModel getChestplateModel() {
        bakeIfNeeded();
        return chestplateModel;
    }

    public static WinemakerLeggingsModel getLeggingsModel() {
        bakeIfNeeded();
        return leggingsModel;
    }

    public static WinemakerBootsModel getBootsModel() {
        bakeIfNeeded();
        return bootsModel;
    }

    /**
     * Render type used on Fabric, where {@code ArmorRenderer} takes over the whole armor layer and therefore has to
     * pick the texture itself. On NeoForge the texture comes from the {@code vinery:winemaker} equipment asset.
     */
    public static RenderType renderType(EquipmentSlot slot) {
        return RenderTypes.armorCutoutNoCull(slot == EquipmentSlot.LEGS ? LEGGINGS_TEXTURE : ARMOR_TEXTURE);
    }

    /**
     * {@code Item.getName()} without a stack is gone in 26.1; the item's own translation key is still the right
     * name for the fixed set-piece list below.
     */
    private static String itemName(Item item) {
        return Component.translatable(item.getDescriptionId()).getString();
    }

    public static void appendToolTip(@NotNull List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        boolean hasFullSet = helmet.getItem() instanceof WinemakerHelmetItem &&
                chestplate.getItem() instanceof WinemakerChestItem &&
                leggings.getItem() instanceof WinemakerLegsItem &&
                boots.getItem() instanceof WinemakerBootsItem;

        ArmorRegistry.setBonusActive = hasFullSet;

        tooltip.add(Component.nullToEmpty(""));
        tooltip.add(Component.nullToEmpty(ChatFormatting.DARK_GREEN + I18n.get("tooltip.vinery.armor.winemaker_armor0")));
        tooltip.add(Component.nullToEmpty((helmet.getItem() instanceof WinemakerHelmetItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + itemName(ObjectRegistry.STRAW_HAT.get().asItem()) + "]"));
        tooltip.add(Component.nullToEmpty((chestplate.getItem() instanceof WinemakerChestItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + itemName(ObjectRegistry.WINEMAKER_APRON.get().asItem()) + "]"));
        tooltip.add(Component.nullToEmpty((leggings.getItem() instanceof WinemakerLegsItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + itemName(ObjectRegistry.WINEMAKER_LEGGINGS.get().asItem()) + "]"));
        tooltip.add(Component.nullToEmpty((boots.getItem() instanceof WinemakerBootsItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + itemName(ObjectRegistry.WINEMAKER_BOOTS.get().asItem()) + "]"));
        tooltip.add(Component.nullToEmpty(""));

        ChatFormatting color = hasFullSet ? ChatFormatting.GREEN : ChatFormatting.GRAY;
        tooltip.add(Component.nullToEmpty(color + I18n.get("tooltip.vinery.armor.winemaker_armor1")));
        tooltip.add(Component.nullToEmpty(color + I18n.get("tooltip.vinery.armor.winemaker_armor2")));
    }
}
