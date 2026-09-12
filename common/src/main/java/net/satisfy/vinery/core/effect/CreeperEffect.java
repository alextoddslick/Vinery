package net.satisfy.vinery.core.effect;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.InstantaneousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CreeperEffect extends InstantaneousMobEffect {
    public CreeperEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }

    @Override
    public void applyInstantaneousEffect(ServerLevel serverLevel, @Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        explode(source, amplifier);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity source, int amplifier) {
        explode(source, amplifier);
        return true;
    }

    private void explode(Entity source, int amplifier){
        if (source instanceof ServerPlayer serverPlayer && serverPlayer.gameMode.getGameModeForPlayer() != GameType.CREATIVE) {
            Level world = serverPlayer.level();
            double x = serverPlayer.getX();
            double y = serverPlayer.getY();
            double z = serverPlayer.getZ();
            world.explode(null, x, y, z, (float) (amplifier), Level.ExplosionInteraction.TNT);

            serverPlayer.hurtServer(serverPlayer.level(), world.damageSources().explosion(null), 50.0F);
        }
    }
}
