package net.satisfy.vinery.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.block.entity.CompletionistBannerEntity;
import net.satisfy.vinery.core.registry.EntityTypeRegistry;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class CompletionistBannerBlock extends BaseEntityBlock implements BlockTooltip {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public CompletionistBannerBlock(Properties properties) {
        super(properties);
        makeDefaultState();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new CompletionistBannerEntity(blockPos, blockState);
    }

    protected void makeDefaultState() {
        registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockState belowBlockState = levelReader.getBlockState(blockPos.below());
        return belowBlockState.isSolid();
    }

    @Override
    public boolean isPossibleToRespawnInThis(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            return this.defaultBlockState().setValue(ROTATION, Mth.floor((double) ((180.0f + context.getRotation()) * 16.0f / 360.0f) + 0.5) & 0xF);
        } else {
            if (this == ObjectRegistry.VINERY_STANDARD.get()) {
                return ObjectRegistry.VINERY_WALL_STANDARD.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, clickedFace.getOpposite());
            }
        }
        return this.defaultBlockState();
    }


    @Override
    protected @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(ROTATION, rotation.rotate(blockState.getValue(ROTATION), 16));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(ROTATION, mirror.mirror(blockState.getValue(ROTATION), 16));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, EntityTypeRegistry.VINERY_STANDARD.get(), (level1, pos, state1, entity) -> CompletionistBannerEntity.tick(level1, pos));
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState blockState, @NotNull LevelReader levelReader, @NotNull ScheduledTickAccess scheduledTickAccess, @NotNull BlockPos blockPos, @NotNull Direction direction, @NotNull BlockPos blockPos2, @NotNull BlockState blockState2, @NotNull RandomSource randomSource) {
        if (direction == Direction.DOWN && !blockState.canSurvive(levelReader, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }

    public Identifier getRenderTexture() {
        return Vinery.identifier("textures/banner/vinery_banner.png");
    }

    @Override
    public void appendBlockHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        if (PlatformHelper.shouldShowTooltip()) {
            tooltip.accept(Component.translatable("tooltip.vinery.banner.thankyou_1").withStyle(style -> style.withColor(TextColor.fromRgb(0x513A8B))));
            tooltip.accept(Component.empty());
            tooltip.accept(Component.translatable("tooltip.vinery.banner.thankyou_2").withStyle(style -> style.withColor(TextColor.fromRgb(0x513A8B))));
            tooltip.accept(Component.translatable("tooltip.vinery.banner.thankyou_4").withStyle(style -> style.withColor(TextColor.fromRgb(0x513A8B))));
            tooltip.accept(Component.empty());
            tooltip.accept(Component.translatable("tooltip.vinery.banner.thankyou_3").withStyle(style -> style.withColor(TextColor.fromRgb(0x513A8B))));
        }
    }
}
