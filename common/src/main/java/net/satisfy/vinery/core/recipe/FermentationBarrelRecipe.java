package net.satisfy.vinery.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedItemContents;
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
import net.satisfy.vinery.core.recipe.input.FermentationBarrelRecipeInput;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FermentationBarrelRecipe implements Recipe<FermentationBarrelRecipeInput> {
    private static final MapCodec<Boolean> WINE_BOTTLE_CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    Codec.BOOL.fieldOf("required").forGetter(b -> b)
            ).apply(inst, b -> b)
    );

    public static final MapCodec<FermentationBarrelRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients")
                    .xmap(FermentationBarrelRecipe::toNonNullList, ingredients -> ingredients)
                    .forGetter(FermentationBarrelRecipe::getInputs),
            FermentationBarrelRecipeInput.JuiceData.CODEC.fieldOf("juice").forGetter(FermentationBarrelRecipe::getJuiceData),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.output),
            WINE_BOTTLE_CODEC.fieldOf("wine_bottle").forGetter(FermentationBarrelRecipe::isWineBottleRequired)
    ).apply(instance, FermentationBarrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FermentationBarrelRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                buf.writeVarInt(recipe.inputs.size());
                for (Ingredient ingredient : recipe.inputs) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                }
                FermentationBarrelRecipeInput.JuiceData.STREAM_CODEC.encode(buf, recipe.getJuiceData());
                ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.output);
                buf.writeBoolean(recipe.wineBottleRequired);
            },
            buf -> {
                int size = buf.readVarInt();
                NonNullList<Ingredient> inputs = NonNullList.createWithCapacity(size);
                for (int i = 0; i < size; i++) {
                    inputs.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                }

                FermentationBarrelRecipeInput.JuiceData juiceData =
                        FermentationBarrelRecipeInput.JuiceData.STREAM_CODEC.decode(buf);
                ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buf);
                boolean wineBottleRequired = buf.readBoolean();

                return new FermentationBarrelRecipe(inputs, juiceData, output, wineBottleRequired);
            }
    );

    private final NonNullList<Ingredient> inputs;
    private final ItemStackTemplate output;
    private final FermentationBarrelRecipeInput.JuiceData juiceData;
    private final boolean wineBottleRequired;
    private PlacementInfo placementInfo;

    public FermentationBarrelRecipe(NonNullList<Ingredient> inputs, FermentationBarrelRecipeInput.JuiceData data, ItemStackTemplate output, boolean wineBottleRequired) {
        this.inputs = inputs;
        this.juiceData = data;
        this.output = output;

        this.wineBottleRequired = wineBottleRequired;
    }

    public static NonNullList<Ingredient> toNonNullList(List<Ingredient> ingredients) {
        NonNullList<Ingredient> list = NonNullList.createWithCapacity(ingredients.size());
        list.addAll(ingredients);
        return list;
    }

    public FermentationBarrelRecipeInput.JuiceData getJuiceData() {
        return this.juiceData;
    }

    public boolean isWineBottleRequired() {
        return wineBottleRequired;
    }

    @Override
    public boolean matches(FermentationBarrelRecipeInput input, Level world) {
        if (this.juiceData.amount() > 0) {
            if (input.data().amount() < this.juiceData.amount()) return false;
            if (!this.juiceData.type().equals(input.data().type())) return false;
        }

        if (this.wineBottleRequired) {
            ItemStack wineBottle = input.getItem(FermentationBarrelRecipeInput.WINE_BOTTLE_SLOT);
            if (wineBottle.isEmpty() || !wineBottle.is(ObjectRegistry.WINE_BOTTLE.get())) return false;
        }

        StackedItemContents recipeMatcher = new StackedItemContents();
        int matchingStacks = 0;

        for (int i = 0; i < input.getIngredientSlots().size(); i++) {
            ItemStack itemStack = input.getIngredientSlots().get(i);
            if (!itemStack.isEmpty()) {
                ++matchingStacks;
                recipeMatcher.accountStack(itemStack, 1);
            }
        }

        return matchingStacks == this.inputs.size() && recipeMatcher.canCraft(this, null);
    }

    @Override
    public @NotNull ItemStack assemble(FermentationBarrelRecipeInput input) {
        return this.output.create();
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    public @NotNull ItemStack getResultItem() {
        return this.output.create();
    }

    public ItemStack getOutput() {
        return this.output.create();
    }

    public NonNullList<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public @NotNull RecipeSerializer<FermentationBarrelRecipe> getSerializer() {
        return RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<FermentationBarrelRecipe> getType() {
        return RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.inputs);
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
