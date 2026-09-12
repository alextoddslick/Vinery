package net.satisfy.vinery.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public class FoodComponent {

	/**
	 * Replacement for the removed {@code FoodProperties.PossibleEffect} record.
	 */
	public record PossibleEffect(MobEffectInstance effect, float probability) {
		public static final Codec<FoodComponent.PossibleEffect> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						MobEffectInstance.CODEC.fieldOf("effect").forGetter(FoodComponent.PossibleEffect::effect),
						Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(FoodComponent.PossibleEffect::probability)
				).apply(instance, FoodComponent.PossibleEffect::new)
		);

		public static final StreamCodec<RegistryFriendlyByteBuf, FoodComponent.PossibleEffect> STREAM_CODEC = StreamCodec.composite(
				MobEffectInstance.STREAM_CODEC, FoodComponent.PossibleEffect::effect,
				ByteBufCodecs.FLOAT, FoodComponent.PossibleEffect::probability,
				FoodComponent.PossibleEffect::new
		);
	}

	private final int nutrition;
	private final float saturationModifier;
	private final boolean canAlwaysEat;
	private final float eatSeconds;
	private final Optional<ItemStack> usingConvertsTo;
	private final List<PossibleEffect> effects;
	private final FoodProperties foodProperties;

	// Codec for serialization/deserialization
	public static final Codec<FoodComponent> DIRECT_CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodComponent::nutrition),
					Codec.FLOAT.fieldOf("saturation").forGetter(FoodComponent::saturationModifier),
					Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FoodComponent::canAlwaysEat),
					ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("eat_seconds", 1.6F).forGetter(FoodComponent::eatSeconds),
					ItemStackTemplate.CODEC.xmap(ItemStackTemplate::create, ItemStackTemplate::fromNonEmptyStack)
							.optionalFieldOf("using_converts_to").forGetter(FoodComponent::usingConvertsTo),
					PossibleEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodComponent::getEffects)
			).apply(instance, FoodComponent::new)
	);

	// Stream codec for network serialization
	public static final StreamCodec<RegistryFriendlyByteBuf, FoodComponent> DIRECT_STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.VAR_INT, FoodComponent::nutrition,
					ByteBufCodecs.FLOAT, FoodComponent::saturationModifier,
					ByteBufCodecs.BOOL, FoodComponent::canAlwaysEat,
					ByteBufCodecs.FLOAT, FoodComponent::eatSeconds,
					ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional), FoodComponent::usingConvertsTo,
					PossibleEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), FoodComponent::getEffects,
					FoodComponent::new
			);

	// Constructor used by codec
	public FoodComponent(int nutrition, float saturationModifier, boolean canAlwaysEat,
						 float eatSeconds, Optional<ItemStack> usingConvertsTo,
						 List<PossibleEffect> effects) {
		this.nutrition = nutrition;
		this.saturationModifier = saturationModifier;
		this.canAlwaysEat = canAlwaysEat;
		this.eatSeconds = eatSeconds;
		this.usingConvertsTo = usingConvertsTo;
		this.effects = effects;
		this.foodProperties = buildFoodProperties(nutrition, saturationModifier, canAlwaysEat);
	}

	// Simple constructor for basic usage
	public FoodComponent(List<PossibleEffect> statusEffects) {
		this(1, 0.0f, true, 1.6F, Optional.empty(), statusEffects);
	}

	// Constructor matching your original method signature
	public FoodComponent(int nutrition, float saturationModifier, boolean canAlwaysEat,
						 boolean fastFood, boolean meat, List<PossibleEffect> statusEffects) {
		this(nutrition, saturationModifier, canAlwaysEat, fastFood ? 0.8F : 1.6F, Optional.empty(), statusEffects);
	}

	private static FoodProperties buildFoodProperties(int nutrition, float saturationModifier, boolean canAlwaysEat) {
		FoodProperties.Builder builder = new FoodProperties.Builder()
				.nutrition(nutrition)
				.saturationModifier(saturationModifier);

		if (canAlwaysEat) {
			builder.alwaysEdible();
		}

		return builder.build();
	}

	// Getters
	public List<PossibleEffect> getEffects() {
		return effects;
	}

	public FoodProperties getFoodProperties() {
		return foodProperties;
	}

	public int nutrition() {
		return nutrition;
	}

	public int getNutrition() {
		return nutrition;
	}

	public float saturationModifier() {
		return saturationModifier;
	}

	public float getSaturationModifier() {
		return saturationModifier;
	}

	public boolean canAlwaysEat() {
		return canAlwaysEat;
	}

	public float eatSeconds() {
		return eatSeconds;
	}

	public Optional<ItemStack> usingConvertsTo() {
		return usingConvertsTo;
	}

	public boolean isFastFood() {
		return eatSeconds < 1.6F;
	}
}
