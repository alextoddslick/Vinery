package net.satisfy.vinery.core.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class WineSettings {

    private final Properties properties;
    private final int baseDuration;

    public WineSettings(String name, Holder<MobEffect> effect, int duration, int strength) {
        this.baseDuration = duration;
        this.properties = GeneralUtil.itemProps(name)
                .food(createWineFoodComponent(), createWineConsumable(effect, duration, strength));
    }

    public Properties getProperties() {
        return properties;
    }

    public int getBaseDuration() {
        return baseDuration;
    }


    private FoodProperties createWineFoodComponent() {
        return new FoodProperties.Builder().alwaysEdible().build();
    }

    private Consumable createWineConsumable(Holder<MobEffect> effect, int duration, int strength) {
        Consumable.Builder builder = Consumables.defaultDrink();
        if (effect != null) {
            builder.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(effect, duration, strength), 1.0F));
        }
        return builder.build();
    }
}
