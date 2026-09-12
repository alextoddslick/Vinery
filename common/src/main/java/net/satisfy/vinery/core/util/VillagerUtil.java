package net.satisfy.vinery.core.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.trading.TradeSet;
import net.satisfy.vinery.core.Vinery;

/**
 * Villager trades are data driven since 26.1: every trade set lives in
 * {@code data/vinery/trade_set/**} and points at a tag of
 * {@code data/vinery/villager_trade/**} entries
 * ({@code data/vinery/tags/villager_trade/**}).
 *
 * <p>This class only holds the {@link ResourceKey}s of Vinery's trade sets; the actual
 * offers are the json files. The winemaker profession is registered with
 * {@link #winemakerTradeSets()} on both platforms, and
 * {@code WanderingWinemakerEntity} pulls {@link #WANDERING_WINEMAKER_COMMON}.
 */
public final class VillagerUtil {
    public static final ResourceKey<TradeSet> WINEMAKER_LEVEL_1 = tradeSet("winemaker/level_1");
    public static final ResourceKey<TradeSet> WINEMAKER_LEVEL_2 = tradeSet("winemaker/level_2");
    public static final ResourceKey<TradeSet> WINEMAKER_LEVEL_3 = tradeSet("winemaker/level_3");
    public static final ResourceKey<TradeSet> WINEMAKER_LEVEL_4 = tradeSet("winemaker/level_4");
    public static final ResourceKey<TradeSet> WINEMAKER_LEVEL_5 = tradeSet("winemaker/level_5");

    public static final ResourceKey<TradeSet> WANDERING_WINEMAKER_COMMON = tradeSet("wandering_winemaker/common");

    private VillagerUtil() {
    }

    public static ResourceKey<TradeSet> tradeSet(String path) {
        return ResourceKey.create(Registries.TRADE_SET, Vinery.identifier(path));
    }

    /**
     * The {@code tradeSetsByLevel} map for the winemaker {@code VillagerProfession} record.
     */
    @SuppressWarnings("unchecked")
    public static Int2ObjectMap<ResourceKey<TradeSet>> winemakerTradeSets() {
        return Int2ObjectMap.ofEntries(
                Int2ObjectMap.entry(1, WINEMAKER_LEVEL_1),
                Int2ObjectMap.entry(2, WINEMAKER_LEVEL_2),
                Int2ObjectMap.entry(3, WINEMAKER_LEVEL_3),
                Int2ObjectMap.entry(4, WINEMAKER_LEVEL_4),
                Int2ObjectMap.entry(5, WINEMAKER_LEVEL_5)
        );
    }
}
