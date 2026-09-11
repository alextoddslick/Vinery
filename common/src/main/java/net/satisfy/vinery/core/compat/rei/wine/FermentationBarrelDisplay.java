package net.satisfy.vinery.core.compat.rei.wine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.recipe.FermentationBarrelRecipe;
import net.satisfy.vinery.core.registry.ObjectRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FermentationBarrelDisplay extends BasicDisplay {

    public static final CategoryIdentifier<FermentationBarrelDisplay> FERMENTATION_BARREL_DISPLAY =
            CategoryIdentifier.of(Vinery.MOD_ID, "fermentation_barrel_display");

    public static final DisplaySerializer<FermentationBarrelDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(FermentationBarrelDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(FermentationBarrelDisplay::getOutputEntries),
                    ResourceLocation.CODEC.optionalFieldOf("location").forGetter(FermentationBarrelDisplay::getDisplayLocation),
                    Codec.INT.fieldOf("juiceAmount").forGetter(FermentationBarrelDisplay::getJuiceAmount),
                    Codec.STRING.fieldOf("juiceType").forGetter(FermentationBarrelDisplay::getJuiceType)
            ).apply(instance, FermentationBarrelDisplay::new)),
            StreamCodec.composite(
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    FermentationBarrelDisplay::getInputEntries,
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    FermentationBarrelDisplay::getOutputEntries,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
                    FermentationBarrelDisplay::getDisplayLocation,
                    ByteBufCodecs.VAR_INT,
                    FermentationBarrelDisplay::getJuiceAmount,
                    ByteBufCodecs.STRING_UTF8,
                    FermentationBarrelDisplay::getJuiceType,
                    FermentationBarrelDisplay::new
            ));

    private final int juiceAmount;
    private final String juiceType;

    public FermentationBarrelDisplay(RecipeHolder<FermentationBarrelRecipe> recipe) {
        this(prepareInputs(recipe.value()), prepareOutputs(recipe.value()), Optional.of(recipe.id().location()),
                recipe.value().getJuiceData().amount(), recipe.value().getJuiceData().type());
    }

    public FermentationBarrelDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, int juiceAmount, String juiceType) {
        this(inputs, outputs, Optional.empty(), juiceAmount, juiceType);
    }

    public FermentationBarrelDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location, int juiceAmount, String juiceType) {
        super(inputs, outputs, location);
        this.juiceAmount = juiceAmount;
        this.juiceType = juiceType;
    }

    public int getJuiceAmount() {
        return juiceAmount;
    }

    public String getJuiceType() {
        return juiceType;
    }

    private static List<EntryIngredient> prepareInputs(FermentationBarrelRecipe recipe) {
        List<EntryIngredient> ingredients = new ArrayList<>(EntryIngredients.ofIngredients(recipe.getIngredients()));
        ingredients.add(EntryIngredients.of(new ItemStack(ObjectRegistry.WINE_BOTTLE.get())));
        return ingredients;
    }

    private static List<EntryIngredient> prepareOutputs(FermentationBarrelRecipe recipe) {
        return Collections.singletonList(EntryIngredients.of(recipe.getResultItem(null)));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return FERMENTATION_BARREL_DISPLAY;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
