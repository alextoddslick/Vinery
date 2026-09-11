package net.satisfy.vinery.core.compat.rei;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.compat.rei.press.ApplePressDisplay;
import net.satisfy.vinery.core.compat.rei.press.ApplePressFermentingDisplay;
import net.satisfy.vinery.core.compat.rei.wine.FermentationBarrelDisplay;
import net.satisfy.vinery.core.recipe.ApplePressFermentingRecipe;
import net.satisfy.vinery.core.recipe.ApplePressMashingRecipe;
import net.satisfy.vinery.core.recipe.FermentationBarrelRecipe;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;

/**
 * Common (server side) half of the REI integration: displays are filled from the server recipe manager
 * and shipped to the client through the registered display serializers.
 */
public class VineryReiCommonPlugin {

    public static void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(FermentationBarrelRecipe.class)
                .filterType(RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get())
                .fill(FermentationBarrelDisplay::new);
        registry.beginRecipeFiller(ApplePressMashingRecipe.class)
                .filterType(RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_TYPE.get())
                .fill(ApplePressDisplay::new);
        registry.beginRecipeFiller(ApplePressFermentingRecipe.class)
                .filterType(RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_TYPE.get())
                .fill(ApplePressFermentingDisplay::new);
    }

    public static void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(Vinery.identifier("wine_fermentation"), FermentationBarrelDisplay.SERIALIZER);
        registry.register(Vinery.identifier("apple_mashing"), ApplePressDisplay.SERIALIZER);
        registry.register(Vinery.identifier("apple_fermenting"), ApplePressFermentingDisplay.SERIALIZER);
    }
}
