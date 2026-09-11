package net.satisfy.vinery.core.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.compat.jei.category.ApplePressFermentingCategory;
import net.satisfy.vinery.core.compat.jei.category.ApplePressMashingCategory;
import net.satisfy.vinery.core.compat.jei.category.FermentationBarrelCategory;
import net.satisfy.vinery.core.compat.jei.transfer.FermentationTransferInfo;
import net.satisfy.vinery.core.network.VineryClientRecipeCache;
import net.satisfy.vinery.core.recipe.FermentationBarrelRecipe;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class VineryJEIPlugin implements IModPlugin {

    /**
     * Clients no longer receive recipes from the server, so Vinery syncs its own recipes into
     * {@link VineryClientRecipeCache}. Depending on the timing the cache may be filled before or after JEI has
     * loaded, so the recipes are pushed into the live runtime as well whenever a new sync packet arrives.
     */
    @Nullable
    private static IJeiRuntime runtime;
    private static boolean listenerRegistered;
    private static final Map<IRecipeType<?>, List<?>> PUSHED = new HashMap<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FermentationBarrelCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ApplePressFermentingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ApplePressMashingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        PUSHED.clear();
        register(registration, FermentationBarrelCategory.FERMENTATION_BARREL, RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get());
        register(registration, ApplePressFermentingCategory.APPLE_PRESS_TYPE, RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_TYPE.get());
        register(registration, ApplePressMashingCategory.APPLE_PRESS_MASHING_TYPE, RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_TYPE.get());
    }

    private static <T extends Recipe<?>> void register(IRecipeRegistration registration, IRecipeType<T> jeiType, RecipeType<T> vanillaType) {
        List<T> recipes = values(vanillaType);
        registration.addRecipes(jeiType, recipes);
        PUSHED.put(jeiType, recipes);
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        if (!listenerRegistered) {
            listenerRegistered = true;
            VineryClientRecipeCache.addUpdateListener(VineryJEIPlugin::refreshRuntimeRecipes);
        }
        refreshRuntimeRecipes();
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    private static void refreshRuntimeRecipes() {
        IJeiRuntime jeiRuntime = runtime;
        if (jeiRuntime == null) return;
        IRecipeManager manager = jeiRuntime.getRecipeManager();
        replace(manager, FermentationBarrelCategory.FERMENTATION_BARREL, RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get());
        replace(manager, ApplePressFermentingCategory.APPLE_PRESS_TYPE, RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_TYPE.get());
        replace(manager, ApplePressMashingCategory.APPLE_PRESS_MASHING_TYPE, RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_TYPE.get());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> void replace(IRecipeManager manager, IRecipeType<T> jeiType, RecipeType<T> vanillaType) {
        List<T> current = values(vanillaType);
        List<T> previous = (List<T>) PUSHED.getOrDefault(jeiType, List.of());

        List<T> stale = new ArrayList<>(previous);
        stale.removeAll(current);
        if (!stale.isEmpty()) manager.hideRecipes(jeiType, stale);

        List<T> added = new ArrayList<>(current);
        added.removeAll(previous);
        if (!added.isEmpty()) manager.addRecipes(jeiType, added);

        PUSHED.put(jeiType, current);
    }

    private static <T extends Recipe<?>> List<T> values(RecipeType<T> type) {
        List<RecipeHolder<T>> holders = VineryClientRecipeCache.get(type);
        List<T> recipes = new ArrayList<>(holders.size());
        for (RecipeHolder<T> holder : holders) {
            recipes.add(holder.value());
        }
        return recipes;
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return Vinery.identifier("jei_plugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new FermentationTransferInfo());

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(FermentationBarrelCategory.FERMENTATION_BARREL, ObjectRegistry.FERMENTATION_BARREL.get().asItem().getDefaultInstance());
        registration.addCraftingStation(ApplePressFermentingCategory.APPLE_PRESS_TYPE, ObjectRegistry.APPLE_PRESS.get().asItem().getDefaultInstance());
        registration.addCraftingStation(ApplePressMashingCategory.APPLE_PRESS_MASHING_TYPE, ObjectRegistry.APPLE_PRESS.get().asItem().getDefaultInstance());
    }

    public static void addSlot(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
        builder.addSlot(RecipeIngredientRole.INPUT, x, y).add(ingredient);
    }

    private static void addItemStackInputSlot(IRecipeLayoutBuilder builder, ItemStack itemStack) {
        builder.addSlot(RecipeIngredientRole.INPUT, 97, 45).add(itemStack);
    }

    private static void addItemStackOutputSlot(IRecipeLayoutBuilder builder, ItemStack itemStack) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 4).add(itemStack);
    }

    public static void buildSlotsFromRecipe(IRecipeLayoutBuilder builder, FermentationBarrelRecipe recipe) {

        final int BOTTOM_ROW_Y = 45;

        final NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
        final int ingredientCount = recipeIngredients.size();
        if (ingredientCount >= 1) VineryJEIPlugin.addSlot(builder, 41, BOTTOM_ROW_Y, recipeIngredients.get(0));
        if (ingredientCount >= 2) VineryJEIPlugin.addSlot(builder, 59, BOTTOM_ROW_Y, recipeIngredients.get(1));
        if (ingredientCount >= 3) VineryJEIPlugin.addSlot(builder, 77, BOTTOM_ROW_Y, recipeIngredients.get(2));

        VineryJEIPlugin.addItemStackInputSlot(builder, ObjectRegistry.WINE_BOTTLE.get().getDefaultInstance());

        VineryJEIPlugin.addItemStackOutputSlot(builder, recipe.getResultItem(null));
    }
}
