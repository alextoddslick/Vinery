package net.satisfy.vinery.core.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.satisfy.vinery.core.Vinery;

public class ArmorMaterialRegistry {
    private static final ArmorMaterial LEATHER = ArmorMaterials.LEATHER;

    /**
     * Equipment asset id of the winemaker set. The resource pack must provide
     * {@code assets/vinery/equipment/winemaker.json}.
     */
    public static final ResourceKey<EquipmentAsset> WINEMAKER_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Vinery.identifier("winemaker"));

    public static final ArmorMaterial WINEMAKER_ARMOR = new ArmorMaterial(
            LEATHER.durability(),
            LEATHER.defense(),
            LEATHER.enchantmentValue(),
            LEATHER.equipSound(),
            LEATHER.toughness(),
            LEATHER.knockbackResistance(),
            LEATHER.repairIngredient(),
            WINEMAKER_ASSET
    );
}
