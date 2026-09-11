package net.satisfy.vinery.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.satisfy.vinery.fabric.config.VineryFabricConfig;
import net.satisfy.vinery.platform.PlatformHelper;

public class PlatformHelperImpl extends PlatformHelper {

    public static int getTotalFermentationTime() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.totalFermentationTime;
    }

    public static int getMaxFluidLevel() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.maxFluidLevel;
    }

    public static int getMaxFluidIncrease() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.maxFluidIncrease;
    }

    public static int getApplePressMashingTime() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.applePressMashingTime;
    }

    public static int getApplePressFermentationTime() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.applePressFermentationTime;
    }

    public static double getCherryGrowthChance() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.cherryGrowthChance;
    }

    public static double getAppleGrowthChance() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.appleGrowthChance;
    }

    public static double getGrapeGrowthChance() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.grapeGrowthChance;
    }

    public static double getGrapeGrowthMultiplier() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.blocks.grapeGrowthMultiplier;
    }

    public static int getWineMaxLevel() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.wine.maxLevel;
    }

    public static int getWineStartDuration() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.wine.startDuration;
    }

    public static int getWineDurationPerYear() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.wine.durationPerYear;
    }

    public static int getWineDaysPerYear() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.wine.daysPerYear;
    }

    public static int getWineYearsPerEffectLevel() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.wine.yearsPerEffectLevel;
    }

    public static int getWineMaxDuration() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.wine.maxDuration;
    }

    public static boolean shouldGiveEffect() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.banner.giveEffect;
    }

    public static boolean shouldShowTooltip() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.items.banner.isShowTooltipEnabled();
    }

    public static double getTraderSpawnChance() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.trader.spawnChance;
    }

    public static boolean shouldSpawnWithMules() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.trader.spawnWithMules;
    }

    public static int getTraderSpawnDelay() {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        return config.trader.spawnDelay;
    }
}