package net.satisfy.vinery.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.recipe.ApplePressMashingRecipe;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class ApplePressMashingCategory implements IRecipeCategory<RecipeHolder<ApplePressMashingRecipe>> {
    public static final IRecipeHolderType<ApplePressMashingRecipe> APPLE_PRESS_MASHING_TYPE =
            IRecipeHolderType.create(Vinery.identifier("apple_press_mashing"));

    private static final int BACKGROUND_WIDTH = 160;
    private static final int BACKGROUND_HEIGHT = 70;
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public ApplePressMashingCategory(IGuiHelper helper) {
        Identifier texture = Identifier.fromNamespaceAndPath("vinery", "textures/gui/apple_press_gui.png");
        this.background = helper.createDrawable(texture, X_OFFSET, Y_OFFSET, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        ItemStack pressStack = new ItemStack(ObjectRegistry.APPLE_PRESS.get());
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, pressStack);
        this.title = ObjectRegistry.APPLE_PRESS.get().getName();
    }

    @NotNull
    @Override
    public IRecipeHolderType<ApplePressMashingRecipe> getRecipeType() {
        return APPLE_PRESS_MASHING_TYPE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return BACKGROUND_WIDTH;
    }

    @Override
    public int getHeight() {
        return BACKGROUND_HEIGHT;
    }

    @Override
    @NotNull
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ApplePressMashingRecipe> holder, IFocusGroup focuses) {
        ApplePressMashingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 44 - X_OFFSET, 34 - Y_OFFSET)
                .add(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 101 - X_OFFSET, 50 - Y_OFFSET)
                .add(recipe.getResultItem());
    }

    @Override
    public void draw(RecipeHolder<ApplePressMashingRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }
}
