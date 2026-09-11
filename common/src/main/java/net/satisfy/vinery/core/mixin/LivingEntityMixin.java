package net.satisfy.vinery.core.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow @Final private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

	@Shadow public abstract boolean addEffect(MobEffectInstance mobEffectInstance);

	protected LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Unique
	private boolean hasStatusEffect(Holder<MobEffect> effect) {
		return activeEffects.containsKey(effect);
	}

	@Inject(method = "calculateFallDamage", at = @At("RETURN"), cancellable = true)
	public void modifyJumpBoostFallDamage(double fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity.hasEffect(MobEffectRegistry.getHolder(MobEffectRegistry.IMPROVED_JUMP_BOOST)) && !entity.hasEffect(MobEffects.JUMP_BOOST)) {
			cir.setReturnValue(Math.max(0, cir.getReturnValue() - 1));
		}
	}

	@Inject(method = "getJumpBoostPower", at = @At(value = "HEAD"), cancellable = true)
	private void improvedJumpBoost(CallbackInfoReturnable<Float> cir) {
		if (this.hasStatusEffect(MobEffectRegistry.getHolder(MobEffectRegistry.IMPROVED_JUMP_BOOST))) {
			MobEffectInstance effect = this.activeEffects.get(MobEffectRegistry.getHolder(MobEffectRegistry.IMPROVED_JUMP_BOOST));
			if (effect != null) {
				cir.setReturnValue(0.1F * (float)(effect.getAmplifier() + 1));
			}
		}
	}
}
