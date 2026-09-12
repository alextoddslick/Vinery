package net.satisfy.vinery.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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
    private static final MapCodec<Boolean> WINE_BOTTLE_CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    Codec.BOOL.fieldOf("required").forGetter(b -> b)
            ).apply(inst, b -> b)
    );

    public static final MapCodec<ApplePressFermentingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("input").forGetter(ApplePressFermentingRecipe::getInput),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.output),
            WINE_BOTTLE_CODEC.fieldOf("wine_bottle").forGetter(ApplePressFermentingRecipe::isRequiresBottle)
    ).apply(inst, ApplePressFermentingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ApplePressFermentingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ApplePressFermentingRecipe::getInput,
            ItemStackTemplate.STREAM_CODEC, r -> r.output,
            ByteBufCodecs.BOOL, ApplePressFermentingRecipe::isRequiresBottle,
            ApplePressFermentingRecipe::new
    );

    public final Ingredient input;
    private final ItemStackTemplate output;
    private final boolean requiresBottle;
    private PlacementInfo placementInfo;

    public ApplePressFermentingRecipe(Ingredient input, ItemStackTemplate output, boolean requiresBottle) {
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
    public @NotNull ItemStack assemble(ApplePressFermentingRecipeInput container) {
        return this.output.create();
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    public @NotNull ItemStack getResultItem() {
        return this.output.create();
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return this.output.create();
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

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NotNull String group() {
        return "";
    }
}
