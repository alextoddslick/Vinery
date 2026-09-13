package net.satisfy.vinery.core.world.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.satisfy.vinery.core.block.GrapeVineBlock;

public record JungleGrapeFeature(BlockState state) implements Feature {

    public static final MapCodec<JungleGrapeFeature> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockState.CODEC.fieldOf("state").forGetter(JungleGrapeFeature::state)
    ).apply(i, JungleGrapeFeature::new));

    @Override
    public MapCodec<JungleGrapeFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {

        int tries = 12;

        int xz = 7;
        int height = 10;

        int length = 12;



        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for(int i = 0; i < tries; i++) {
            mutable.set(origin).move(
                    random.nextInt((xz * 2) + 1) - xz,
                    random.nextInt(height) - 1,
                    random.nextInt((xz * 2) + 1) - xz
            );


            if(!level.isEmptyBlock(mutable)) {
                continue;
            }

            BlockPos.MutableBlockPos vineMutablePos = new BlockPos.MutableBlockPos().set(mutable);
            ChunkPos currentChunkPos = ChunkPos.containing(vineMutablePos);
            BlockState currentBlockstate;
            BlockState aboveBlockstate;

            int maxLength = length - random.nextInt(random.nextInt(length) + 1);
            int targetY = vineMutablePos.getY() - maxLength;

            for (; vineMutablePos.getY() >= targetY; vineMutablePos.move(Direction.DOWN)) {
                if (level.isEmptyBlock(vineMutablePos)) {
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        mutable.set(vineMutablePos).move(direction);
                        ChunkPos newChunkPos = ChunkPos.containing(mutable);

                        if(newChunkPos.x() != currentChunkPos.x() || newChunkPos.z() != currentChunkPos.z()) continue;

                        currentBlockstate = this.state.setValue(GrapeVineBlock.getPropertyForFace(direction), true);
                        aboveBlockstate = level.getBlockState(vineMutablePos.above());

                        if (currentBlockstate.canSurvive(level, vineMutablePos) && level.getBlockState(vineMutablePos.relative(direction)).getBlock() != Blocks.MOSS_CARPET) {
                            level.setBlock(vineMutablePos, currentBlockstate.setValue(VineBlock.UP, aboveBlockstate.canOcclude()).setValue(GrapeVineBlock.AGE, random.nextInt(3)), 2);
                            break;
                        }
                        else if (aboveBlockstate.is(this.state.getBlock())) {
                            level.setBlock(vineMutablePos, aboveBlockstate.setValue(VineBlock.UP, false).setValue(GrapeVineBlock.AGE, random.nextInt(3)), 2);
                            break;
                        }
                    }
                }
                else {
                    break;
                }
            }
        }

        return true;
    }
}
