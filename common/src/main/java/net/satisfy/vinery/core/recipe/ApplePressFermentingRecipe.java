package net.satisfy.vinery.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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
import net.satisfy.vinery.core.recipe.input.ApplePressFermentingRecipeInput;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class ApplePressFermentingRecipe implements Recipe<ApplePressFermentingRecipeInput> {
    public final Ingredient input;
    private final ItemStack output;
    private final boolean requiresBottle;
    private PlacementInfo placementInfo;

    public ApplePressFermentingRecipe(Ingredient input, ItemStack output, boolean requiresBottle) {
        this.input = input;
        this.output = output;
        this.requiresBottle = requiresBottle;
    }

    public boolean requiresBottle() {
        return requiresBottle;
    }

    @Override
    public boolean matches(ApplePressFermentingRecipeInput inventory, Level world) {
        return input.test(inventory.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(ApplePressFermentingRecipeInput container, HolderLookup.Provider registryAccess) {
        return this.output.copy();
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    public @NotNull ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return this.output.copy();
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public boolean isRequiresBottle() {
        return requiresBottle;
    }

    @Override
    public @NotNull RecipeSerializer<ApplePressFermentingRecipe> getSerializer() {
        return RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<ApplePressFermentingRecipe> getType() {
        return RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_TYPE.get();
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

    public static class Serializer implements RecipeSerializer<ApplePressFermentingRecipe> {

        private static final MapCodec<Boolean> WINE_BOTTLE_CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Codec.BOOL.fieldOf("required").forGetter(b -> b)
                ).apply(inst, b -> b)
        );

        @Override
        public @NotNull MapCodec<ApplePressFermentingRecipe> codec() {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(ApplePressFermentingRecipe::getInput),
                    ItemStack.CODEC.fieldOf("output").forGetter(ApplePressFermentingRecipe::getOutput),
                    WINE_BOTTLE_CODEC.fieldOf("wine_bottle").forGetter(ApplePressFermentingRecipe::isRequiresBottle)
            ).apply(inst, ApplePressFermentingRecipe::new));
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ApplePressFermentingRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public @NotNull ApplePressFermentingRecipe decode(RegistryFriendlyByteBuf buf) {
                    return new ApplePressFermentingRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), ItemStack.STREAM_CODEC.decode(buf), buf.readBoolean());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ApplePressFermentingRecipe recipe) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getInput());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.getOutput());
                    buf.writeBoolean(recipe.isRequiresBottle());
                }
            };
        }
    }
}
