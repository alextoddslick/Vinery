package net.satisfy.vinery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.satisfy.vinery.client.gui.handler.ApplePressGuiHandler;
import net.satisfy.vinery.core.recipe.ApplePressFermentingRecipe;
import net.satisfy.vinery.core.recipe.ApplePressMashingRecipe;
import net.satisfy.vinery.core.recipe.input.ApplePressFermentingRecipeInput;
import net.satisfy.vinery.core.recipe.input.ApplePressMashingRecipeInput;
import net.satisfy.vinery.core.registry.EntityTypeRegistry;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import net.satisfy.vinery.core.util.ImplementedInventory;
import net.satisfy.vinery.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ApplePressBlockEntity extends BlockEntity implements MenuProvider, ImplementedInventory, BlockEntityTicker<ApplePressBlockEntity> {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    protected final ContainerData propertyDelegate;
    private int progress1 = 0;
    private int maxProgress1 = PlatformHelper.getApplePressMashingTime();
    private int progress2 = 0;
    private int maxProgress2 = PlatformHelper.getApplePressFermentationTime();

    public ApplePressBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.APPLE_PRESS_BLOCK_ENTITY.get(), pos, state);
        this.propertyDelegate = new ContainerData() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> ApplePressBlockEntity.this.progress1;
                    case 1 -> ApplePressBlockEntity.this.maxProgress1;
                    case 2 -> ApplePressBlockEntity.this.progress2;
                    case 3 -> ApplePressBlockEntity.this.maxProgress2;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        ApplePressBlockEntity.this.progress1 = value;
                        break;
                    case 1:
                        ApplePressBlockEntity.this.maxProgress1 = value;
                        break;
                    case 2:
                        ApplePressBlockEntity.this.progress2 = value;
                        break;
                    case 3:
                        ApplePressBlockEntity.this.maxProgress2 = value;
                        break;
                }
            }

            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{3};
        } else if (side.getAxis().isHorizontal()) {
            return new int[]{0, 1, 2};
        }
        return new int[]{};
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new ApplePressGuiHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ContainerHelper.saveAllItems(valueOutput, this.inventory);
        valueOutput.putInt("apple_press.progress1", progress1);
        valueOutput.putInt("apple_press.progress2", progress2);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        ContainerHelper.loadAllItems(valueInput, this.inventory);
        progress1 = valueInput.getIntOr("apple_press.progress1", 0);
        progress2 = valueInput.getIntOr("apple_press.progress2", 0);
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, ApplePressBlockEntity entity) {
        if (!(world instanceof ServerLevel serverLevel)) return;
        final RecipeManager recipeManager = serverLevel.recipeAccess();

        boolean dirty = false;

        if (hasInput(entity, 0)) {
            ApplePressMashingRecipeInput input = new ApplePressMashingRecipeInput(entity.getItem(0));
            Optional<RecipeHolder<ApplePressMashingRecipe>> mashing =
                    recipeManager.getRecipeFor(RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_TYPE.get(), input, world);
            if (mashing.isEmpty()) {
                return;
            }
            ApplePressMashingRecipe mashingRecipe = mashing.get().value();
            if (canProcessMashing(entity, mashingRecipe, input)) {
                entity.progress1++;
                if (entity.progress1 >= entity.maxProgress1) {
                    processMashing(entity, mashingRecipe, input);
                    dirty = true;
                }
            } else {
                entity.progress1 = 0;
            }
        } else {
            entity.progress1 = 0;
        }

        if (hasInput(entity, 1)) {
            ApplePressFermentingRecipeInput input = new ApplePressFermentingRecipeInput(entity.getItem(1));
            Optional<RecipeHolder<ApplePressFermentingRecipe>> fermenting =
                    recipeManager.getRecipeFor(RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_TYPE.get(), input, world);
            if (fermenting.isEmpty()) return;
            ApplePressFermentingRecipe fermentingRecipe = fermenting.get().value();
            if (canProcessFermenting(entity, fermentingRecipe, input)) {
                entity.progress2++;
                if (entity.progress2 >= entity.maxProgress2) {
                    processFermenting(entity, fermentingRecipe, input);
                    dirty = true;
                }
            } else {
                entity.progress2 = 0;
            }
        } else {
            entity.progress2 = 0;
        }

        if (dirty) {
            setChanged(world, pos, state);
        }
    }

    private static boolean hasInput(ApplePressBlockEntity entity, int slot) {
        return !entity.getItem(slot).isEmpty();
    }

    private static boolean canProcessMashing(ApplePressBlockEntity entity, ApplePressMashingRecipe recipe, ApplePressMashingRecipeInput input) {
        assert entity.level != null;
        ItemStack output = entity.getItem(1);
        if (!recipe.matches(input, entity.level)) return false;
        if (output.isEmpty()) return true;
        return output.getItem() == recipe.assemble(input, entity.level.registryAccess()).getItem();
    }

    private static void processMashing(ApplePressBlockEntity entity, ApplePressMashingRecipe recipe, ApplePressMashingRecipeInput input) {
        assert entity.level != null;
        ItemStack result = recipe.assemble(input, entity.level.registryAccess()).copy();
        entity.removeItem(0, 1);
        ItemStack outputSlot = entity.getItem(1);
        if (outputSlot.isEmpty()) {
            entity.setItem(1, result);
        } else {
            outputSlot.grow(result.getCount());
        }
        entity.progress1 = 0;
    }

    private static boolean canProcessFermenting(ApplePressBlockEntity entity, ApplePressFermentingRecipe recipe, ApplePressFermentingRecipeInput input) {
        assert entity.level != null;
        if (!recipe.matches(input, entity.level)) return false;
        if (recipe.requiresBottle()) {
            ItemStack bottle = entity.getItem(2);
            if (!isWineBottle(bottle)) return false;
        }
        ItemStack output = entity.getItem(3);
        if (output.isEmpty()) return true;
        return output.getItem() == recipe.assemble(input, entity.level.registryAccess()).getItem();
    }

    private static void processFermenting(ApplePressBlockEntity entity, ApplePressFermentingRecipe recipe, ApplePressFermentingRecipeInput input) {
        assert entity.level != null;
        ItemStack result = recipe.assemble(input, entity.level.registryAccess()).copy();
        entity.removeItem(1, 1);
        if (recipe.requiresBottle()) {
            entity.removeItem(2, 1);
        }
        ItemStack outputSlot = entity.getItem(3);
        if (outputSlot.isEmpty()) {
            entity.setItem(3, result);
        } else {
            outputSlot.grow(result.getCount());
        }
        entity.progress2 = 0;
    }

    private static boolean isWineBottle(ItemStack stack) {
        return stack.getItem() == ObjectRegistry.WINE_BOTTLE.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level != null && this.level.getBlockEntity(this.worldPosition) == this && player.distanceToSqr((double) this.worldPosition.getX() + 0.5, (double) this.worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return switch (index) {
            case 0 -> true;
            case 2 -> stack.getItem() == ObjectRegistry.WINE_BOTTLE.get();
            default -> false;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        assert direction != null;
        if (direction.getAxis().isHorizontal()) {
            return switch (index) {
                case 0 -> isValidForApplePressMashing(stack);
                case 1 -> isValidForApplePressFermenting(stack);
                case 2 -> isWineBottle(stack);
                default -> false;
            };
        }
        return false;
    }


    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == 3 && (direction == Direction.DOWN || direction.getAxis().isHorizontal());
    }

    private boolean isValidForApplePressMashing(ItemStack stack) {
        if (!(this.level instanceof ServerLevel serverLevel)) return false;
        for (RecipeHolder<?> holder : serverLevel.recipeAccess().getRecipes()) {
            if (holder.value() instanceof ApplePressMashingRecipe recipe && recipe.getInput().test(stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidForApplePressFermenting(ItemStack stack) {
        if (!(this.level instanceof ServerLevel serverLevel)) return false;
        for (RecipeHolder<?> holder : serverLevel.recipeAccess().getRecipes()) {
            if (holder.value() instanceof ApplePressFermentingRecipe recipe && recipe.getInput().test(stack)) {
                return true;
            }
        }
        return false;
    }
}
