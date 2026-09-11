package net.satisfy.vinery.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.recipe.ApplePressMashingRecipe;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class ApplePressMashingCategory implements IRecipeCategory<ApplePressMashingRecipe> {
    public static final IRecipeType<ApplePressMashingRecipe> APPLE_PRESS_MASHING_TYPE =
            IRecipeType.create(Vinery.MOD_ID, "apple_press_mashing", ApplePressMashingRecipe.class);

    private static final int BACKGROUND_WIDTH = 160;
    private static final int BACKGROUND_HEIGHT = 70;
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public ApplePressMashingCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("vinery", "textures/gui/apple_press_gui.png");
        this.background = helper.createDrawable(texture, X_OFFSET, Y_OFFSET, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        ItemStack pressStack = new ItemStack(ObjectRegistry.APPLE_PRESS.get());
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, pressStack);
        this.title = ObjectRegistry.APPLE_PRESS.get().getName();
    }

    @NotNull
    @Override
    public IRecipeType<ApplePressMashingRecipe> getRecipeType() {
        return APPLE_PRESS_MASHING_TYPE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return title;
    }

    @NotNull
    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    @NotNull
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ApplePressMashingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 44 - X_OFFSET, 34 - Y_OFFSET)
                .add(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 101 - X_OFFSET, 50 - Y_OFFSET)
                .add(recipe.getResultItem(null));
    }
}
