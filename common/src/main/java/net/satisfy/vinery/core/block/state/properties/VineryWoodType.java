package net.satisfy.vinery.core.block.state.properties;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class VineryWoodType {
    /**
     * The name must be a valid resource-location <em>path</em>, so it may not contain a {@code ':'}.
     * Since 26.2 both sign edit screens build their background texture from it:
     * {@code SignEditScreen} (new in 26.2) uses
     * {@code Identifier.withDefaultNamespace("textures/gui/signs/" + woodType.name() + ".png")} and
     * {@code HangingSignEditScreen} uses {@code ".../hanging_signs/" + woodType.name() + ".png"}.
     * {@code withDefaultNamespace} throws {@code IdentifierException} on a path containing {@code ':'},
     * so the old namespaced name ({@code "vinery:dark_cherry"}) crashed the client as soon as a dark
     * cherry sign was placed or right-clicked.
     *
     * <p>{@code WoodType.name()} is used for nothing else in 26.2 (the sign body is a block model now and
     * {@code Sheets.SIGN_SPRITES} is gone), apart from the key of the global {@code WoodType.TYPES} map.
     * The two GUI textures consequently live in the {@code minecraft} namespace:
     * {@code assets/minecraft/textures/gui/signs/dark_cherry.png} and
     * {@code assets/minecraft/textures/gui/hanging_signs/dark_cherry.png}.
     */
    public static final WoodType DARK_CHERRY = WoodType.register(new WoodType("dark_cherry", BlockSetType.OAK));
}
