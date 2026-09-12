package net.satisfy.vinery.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpreadingSnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.SpreadableGrassSlabBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

/**
 * 26.1 renamed {@code SpreadingSnowyDirtBlock} to {@link SpreadingSnowyBlock} and made the
 * block it spreads onto data driven, so {@code randomTick} now carries two extra locals
 * ({@code Registry<Block> blocks}, {@code Optional<Block> baseBlock}) before the spread loop.
 *
 * <p>The injection point is the {@code getBlockState(testPos).is(baseBlock.get())} check inside
 * the 4-attempt spread loop - the only {@code BlockState#is} call in the method. Note that
 * {@code is} now erases to {@code is(Object)} ({@code TypedInstance#is(T)}), not
 * {@code is(Block)}.
 */
@Mixin(SpreadingSnowyBlock.class)
public class SpreadingSnowyBlockMixin {

    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void vinery$spreadToGrassSlabs(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci,
                                          Registry<Block> blocks, Optional<Block> baseBlock, BlockState defaultBlockState,
                                          int attempt, BlockPos testPos) {
        SpreadableGrassSlabBlock.trySpread(level, testPos);
    }
}
