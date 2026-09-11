package net.satisfy.vinery.platform.neoforge;

import net.satisfy.vinery.neoforge.core.config.VineryForgeConfig;
import net.satisfy.vinery.platform.PlatformHelper;

@SuppressWarnings("unused")
public class PlatformHelperImpl extends PlatformHelper {

    public static int getTotalFermentationTime() {
        return VineryForgeConfig.totalFermentationTimeCache;
    }

    public static int getMaxFluidLevel() {
        return VineryForgeConfig.maxFluidLevelCache;
    }

    public static int getMaxFluidIncrease() {
        return VineryForgeConfig.maxFluidIncreaseCache;
    }

    public static int getApplePressMashingTime() {
        return VineryForgeConfig.applePressMashingTimeCache;
    }

    public static int getApplePressFermentationTime() {
        return VineryForgeConfig.applePressFerentingTimeCache;
    }

    public static double getCherryGrowthChance() {
        return VineryForgeConfig.cherryGrowthChanceCache;
    }

    public static double getAppleGrowthChance() {
        return VineryForgeConfig.appleGrowthChanceCache;
    }

    public static double getGrapeGrowthChance() {
        return VineryForgeConfig.grapeGrowthChanceCache;
    }

    public static double getGrapeGrowthMultiplier() {
        return VineryForgeConfig.grapeGrowthMultiplierCache;
    }

    public static int getWineMaxLevel() {
        return VineryForgeConfig.maxLevelCache;
    }

    public static int getWineStartDuration() {
        return VineryForgeConfig.startDurationCache;
    }

    public static int getWineDurationPerYear() {
        return VineryForgeConfig.durationPerYearCache;
    }

    public static int getWineDaysPerYear() {
        return VineryForgeConfig.daysPerYearCache;
    }

    public static int getWineYearsPerEffectLevel() {
        return VineryForgeConfig.yearsPerEffectLevelCache;
    }

    public static int getWineMaxDuration() {
        return VineryForgeConfig.maxDurationCache;
    }

    public static boolean shouldGiveEffect() {
        return VineryForgeConfig.giveEffectCache;
    }

    public static boolean shouldShowTooltip() {
        return VineryForgeConfig.giveEffectCache && VineryForgeConfig.showTooltipCache;
    }

    public static double getTraderSpawnChance() {
        return VineryForgeConfig.traderSpawnChanceCache;
    }

    public static boolean shouldSpawnWithMules() {
        return VineryForgeConfig.spawnWithMulesCache;
    }

    public static int getTraderSpawnDelay() {
        return VineryForgeConfig.traderSpawnDelayCache;
    }
}