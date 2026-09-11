package net.satisfy.vinery.core.compat.rei;

import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.satisfy.vinery.core.compat.rei.press.ApplePressCategory;
import net.satisfy.vinery.core.compat.rei.press.ApplePressDisplay;
import net.satisfy.vinery.core.compat.rei.press.ApplePressFermentingCategory;
import net.satisfy.vinery.core.compat.rei.press.ApplePressFermentingDisplay;
import net.satisfy.vinery.core.compat.rei.wine.FermentationBarrelCategory;
import net.satisfy.vinery.core.compat.rei.wine.FermentationBarrelDisplay;
import net.satisfy.vinery.core.registry.ObjectRegistry;

/**
 * Client half of the REI integration. Since REI 18 the client no longer has access to the recipe manager,
 * so displays are built on the server by {@link VineryReiCommonPlugin} and only the categories live here.
 */
public class VineryReiClientPlugin {
    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new FermentationBarrelCategory());
        registry.add(new ApplePressCategory());
        registry.add(new ApplePressFermentingCategory());
        registry.addWorkstations(FermentationBarrelDisplay.FERMENTATION_BARREL_DISPLAY, EntryStacks.of(ObjectRegistry.FERMENTATION_BARREL.get()));
        registry.addWorkstations(ApplePressDisplay.APPLE_PRESS_DISPLAY, EntryStacks.of(ObjectRegistry.APPLE_PRESS.get()));
        registry.addWorkstations(ApplePressFermentingDisplay.APPLE_PRESS_DISPLAY, EntryStacks.of(ObjectRegistry.APPLE_PRESS.get()));
    }
}
