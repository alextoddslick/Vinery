package net.satisfy.vinery.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.3 replacement for the old {@code ShovelItemMixin}: shovel flattening now runs through the
 * {@link BlockTransformer} item component, so guard {@code transformBlock} instead of {@code ShovelItem#useOn}.
 * Single (non-double) slabs must never be flattened into a full dirt path block.
 */
@Mixin(BlockTransformer.class)
public class BlockTransformerMixin {
    @Inject(method = "transformBlock", at = @At(value = "HEAD"), cancellable = true)
    public void canConvertSlab(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (!useOnContext.getItemInHand().is(ItemTags.SHOVELS)) {
            return;
        }
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(blockState.getBlock() instanceof SlabBlock && !blockState.getValue(SlabBlock.TYPE).equals(SlabType.DOUBLE)){
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

}
