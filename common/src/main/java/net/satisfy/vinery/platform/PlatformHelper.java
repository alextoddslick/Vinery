package net.satisfy.vinery.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlatformHelper {

    @ExpectPlatform
    public static int getTotalFermentationTime() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getMaxFluidLevel() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getMaxFluidIncrease() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getApplePressMashingTime() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getApplePressFermentationTime() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getCherryGrowthChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getAppleGrowthChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getGrapeGrowthChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWineMaxLevel() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWineStartDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWineDurationPerYear() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWineDaysPerYear() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWineYearsPerEffectLevel() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWineMaxDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean shouldGiveEffect() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean shouldShowTooltip() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getTraderSpawnChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean shouldSpawnWithMules() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getTraderSpawnDelay() {
        throw new AssertionError();
    }

    /**
     * Adds an axe stripping interaction (input -> result, block state properties such as AXIS are copied).
     * Must be called during mod init, before the {@code block_transformer} datapack registry is loaded.
     */
    @ExpectPlatform
    public static void registerStrippable(Block input, Block result) {
        throw new AssertionError();
    }

    /**
     * Adds a shovel flattening interaction (input -> result state); like vanilla it requires air above the block.
     * Must be called during mod init, before the {@code block_transformer} datapack registry is loaded.
     */
    @ExpectPlatform
    public static void registerFlattenable(Block input, BlockState result) {
        throw new AssertionError();
    }
}