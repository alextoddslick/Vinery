package net.satisfy.vinery.core.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.recipe.input.ApplePressMashingRecipeInput;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class ApplePressMashingRecipe implements Recipe<ApplePressMashingRecipeInput> {
    public static final MapCodec<ApplePressMashingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("input").forGetter(ApplePressMashingRecipe::getInput),
            ItemStack.CODEC.fieldOf("output").forGetter(ApplePressMashingRecipe::getOutput)
    ).apply(inst, ApplePressMashingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ApplePressMashingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ApplePressMashingRecipe::getInput,
            ItemStack.STREAM_CODEC, ApplePressMashingRecipe::getOutput,
            ApplePressMashingRecipe::new
    );

    public final Ingredient input;
    private final ItemStack output;
    private PlacementInfo placementInfo;

    public ApplePressMashingRecipe(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(ApplePressMashingRecipeInput inventory, Level world) {
        return input.test(inventory.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(ApplePressMashingRecipeInput container) {
        return this.output.copy();
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    public @NotNull ItemStack getResultItem() {
        return this.output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<ApplePressMashingRecipe> getSerializer() {
        return RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<ApplePressMashingRecipe> getType() {
        return RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.input);
        }
        return this.placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NotNull String group() {
        return "";
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}
