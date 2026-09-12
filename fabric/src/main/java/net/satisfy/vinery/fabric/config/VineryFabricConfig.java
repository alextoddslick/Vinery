package net.satisfy.vinery.fabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "vinery")
@Config.Gui.Background("vinery:textures/block/dark_cherry_planks.png")
public class VineryFabricConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public BlocksSettings blocks = new BlocksSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ItemsSettings items = new ItemsSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public TraderSettings trader = new TraderSettings();

    public static class BlocksSettings {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        public int totalFermentationTime = 6000;

        @ConfigEntry.BoundedDiscrete(min = 100, max = 10000)
        public int maxFluidLevel = 100;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        public int maxFluidIncrease = 25;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        public int applePressMashingTime = 600;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        public int applePressFermentationTime = 800;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        public double cherryGrowthChance = 0.4;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        public double appleGrowthChance = 0.4;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        public double grapeGrowthChance = 0.5;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        public double grapeGrowthMultiplier = 1.0;
    }

    public static class ItemsSettings {
        @ConfigEntry.Gui.CollapsibleObject
        public WineSettings wine = new WineSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public BannerSettings banner = new BannerSettings();

        public static class WineSettings {
            @ConfigEntry.BoundedDiscrete(min = 1, max = 100000)
            public int startDuration = 1800;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100000)
            public int maxDuration = 15000;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
            public int maxLevel = 5;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
            public int durationPerYear = 200;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
            public int daysPerYear = 24;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
            public int yearsPerEffectLevel = 6;
        }
    }

    public static class BannerSettings {
        public boolean giveEffect = true;
        public boolean showTooltip = true;

        public boolean isShowTooltipEnabled() {
            return giveEffect && showTooltip;
        }
    }

    public static class TraderSettings {
        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        public double spawnChance = 0.5;

        public boolean spawnWithMules = true;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int spawnDelay = 48000;
    }
}
