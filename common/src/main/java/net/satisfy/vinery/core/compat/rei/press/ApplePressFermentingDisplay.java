package net.satisfy.vinery.core.compat.rei.press;

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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.recipe.ApplePressFermentingRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("all")
public class ApplePressFermentingDisplay extends BasicDisplay {

    public static final CategoryIdentifier<ApplePressFermentingDisplay> APPLE_PRESS_DISPLAY = CategoryIdentifier.of(Vinery.MOD_ID, "apple_press_fermenting_display");

    public static final DisplaySerializer<ApplePressFermentingDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(ApplePressFermentingDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(ApplePressFermentingDisplay::getOutputEntries),
                    ResourceLocation.CODEC.optionalFieldOf("location").forGetter(ApplePressFermentingDisplay::getDisplayLocation)
            ).apply(instance, ApplePressFermentingDisplay::new)),
            StreamCodec.composite(
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    ApplePressFermentingDisplay::getInputEntries,
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    ApplePressFermentingDisplay::getOutputEntries,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
                    ApplePressFermentingDisplay::getDisplayLocation,
                    ApplePressFermentingDisplay::new
            ));

    public ApplePressFermentingDisplay(RecipeHolder<ApplePressFermentingRecipe> recipe) {
        this(Collections.singletonList(EntryIngredients.ofIngredient(recipe.value().input)),
                Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(null))),
                Optional.of(recipe.id().location()));
    }

    public ApplePressFermentingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        this(inputs, outputs, Optional.empty());
    }

    public ApplePressFermentingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
        super(inputs, outputs, location);
    }


    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return APPLE_PRESS_DISPLAY;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
