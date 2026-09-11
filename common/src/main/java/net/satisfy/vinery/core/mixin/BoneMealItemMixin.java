package net.satisfy.vinery.core.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {

    @Inject(method = "useOn", at = @At("RETURN"), cancellable = true)
    public void useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.SUCCESS) {
            return;
        }

        if (!(context.getLevel() instanceof ServerLevel)) {
            return;
        }

        Player player = context.getPlayer();
        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        boolean hasFullSet = helmet.is(ObjectRegistry.STRAW_HAT.get()) &&
                chestplate.is(ObjectRegistry.WINEMAKER_APRON.get()) &&
                leggings.is(ObjectRegistry.WINEMAKER_LEGGINGS.get()) &&
                boots.is(ObjectRegistry.WINEMAKER_BOOTS.get());

        if (hasFullSet) {
            cir.setReturnValue(InteractionResult.PASS);
            ItemStack heldItem = context.getItemInHand();
            if (!heldItem.isEmpty()) {
                heldItem.grow(1);
            }

            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
                ItemStack armorPiece = player.getItemBySlot(slot);
                if (!armorPiece.isEmpty()) {
                    armorPiece.hurtAndBreak(2, player, slot);
                }
            }
        }
    }
}
