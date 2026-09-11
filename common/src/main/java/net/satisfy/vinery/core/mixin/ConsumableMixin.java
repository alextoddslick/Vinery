package net.satisfy.vinery.core.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.registry.DataComponentRegistry;
import net.satisfy.vinery.core.util.FoodComponent;
import net.satisfy.vinery.core.util.WineYears;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 1.21.10 replacement for the old {@code LivingEntity#eat(Level, ItemStack, FoodProperties)} hook:
 * applies the effects of Vinery's {@code custom_food} data component when the stack is consumed.
 */
@Mixin(Consumable.class)
public abstract class ConsumableMixin {

	@Inject(method = "onConsume", at = @At("HEAD"))
	private void applyFoodEffects(Level world, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) {
		if (stack.has(DataComponentRegistry.CUSTOM_FOOD.get())) {
			FoodComponent foodComponent = stack.get(DataComponentRegistry.CUSTOM_FOOD.get());
			if (foodComponent != null) {
				List<FoodComponent.PossibleEffect> list = foodComponent.getEffects();
				for (FoodComponent.PossibleEffect effect : list) {
					if (world.isClientSide() || effect.effect() == null || !(world.random.nextFloat() < effect.probability())) continue;
					MobEffectInstance statusEffectInstance = new MobEffectInstance(effect.effect());
					statusEffectInstance.amplifier = WineYears.getEffectLevel(stack, world);
					if (statusEffectInstance.getEffect().equals(MobEffects.INSTANT_HEALTH) || statusEffectInstance.getEffect().equals(MobEffects.INSTANT_DAMAGE)) {
						statusEffectInstance.duration = 1;
					}
					entity.addEffect(statusEffectInstance);
				}
			}
		}
	}
}
