package net.satisfy.vinery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.satisfy.vinery.core.registry.EntityTypeRegistry;
import net.satisfy.vinery.core.registry.GrapeTypeRegistry;
import net.satisfy.vinery.core.util.GrapeType;
import org.jetbrains.annotations.NotNull;

public class LatticeBlockEntity extends BlockEntity {
    private int age = 0;
    private GrapeType grape = GrapeTypeRegistry.NONE;
    private boolean showHanging;
    private boolean initialized;

    public LatticeBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.LATTICE.get(), pos, state);
    }

    public boolean shouldShowHanging() {
        return showHanging;
    }

    public void setAge(int age) {
        this.age = age;
        setChanged();
        sync();
    }

    public int getAge() {
        return age;
    }

    public void setGrapeType(GrapeType grape) {
        this.grape = grape;
        if (!initialized && level != null) {
            this.showHanging = level.random.nextFloat() < 0.15f;
            this.initialized = true;
        }
        setChanged();
        sync();
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.age = valueInput.getIntOr("Age", 0);
        this.grape = GrapeType.fromString(valueInput.getStringOr("Grape", ""));
        this.showHanging = valueInput.getBooleanOr("ShowHanging", false);
        this.initialized = true;
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("Age", age);
        valueOutput.putString("Grape", grape.getSerializedName());
        valueOutput.putBoolean("ShowHanging", showHanging);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void sync() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
