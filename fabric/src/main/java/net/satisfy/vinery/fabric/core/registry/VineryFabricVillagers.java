package net.satisfy.vinery.fabric.core.registry;

import com.google.common.collect.ImmutableSet;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.util.VillagerUtil;
import net.satisfy.vinery.fabric.config.VineryFabricConfig;

public class VineryFabricVillagers {

    private static final ResourceLocation WINEMAKER_POI_IDENTIFIER = Vinery.identifier("winemaker_poi");
    private static final ResourceLocation WINEMAKER_IDENTIFIER = Vinery.identifier("winemaker");

    public static final ResourceKey<PoiType> WINEMAKER_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, WINEMAKER_POI_IDENTIFIER);
    public static final ResourceKey<VillagerProfession> WINEMAKER_KEY = ResourceKey.create(Registries.VILLAGER_PROFESSION, WINEMAKER_IDENTIFIER);

    public static final PoiType WINEMAKER_POI;
    public static final VillagerProfession WINEMAKER;

    static {
        WINEMAKER_POI = PointOfInterestHelper.register(
                WINEMAKER_POI_IDENTIFIER, 1, 12, ObjectRegistry.FERMENTATION_BARREL.get()
        );

        WINEMAKER = Registry.register(
                BuiltInRegistries.VILLAGER_PROFESSION,
                WINEMAKER_KEY,
                new VillagerProfession(
                        Component.translatable("entity.minecraft.villager.vinery.winemaker"),
                        holder -> holder.is(WINEMAKER_POI_KEY),
                        holder -> holder.is(WINEMAKER_POI_KEY),
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.VILLAGER_WORK_FARMER
                )
        );
    }

    public static void registerPOIAndProfession() {
    }

    public static void init(MinecraftServer server) {
        VineryFabricConfig config = AutoConfig.getConfigHolder(VineryFabricConfig.class).getConfig();
        RegistryAccess registryAccess = server.registryAccess();

        registerTradesForLevel(config.villager.level1, 1, registryAccess);
        registerTradesForLevel(config.villager.level2, 2, registryAccess);
        registerTradesForLevel(config.villager.level3, 3, registryAccess);
        registerTradesForLevel(config.villager.level4, 4, registryAccess);
        registerTradesForLevel(config.villager.level5, 5, registryAccess);
    }

    private static void registerTradesForLevel(VineryFabricConfig.VillagerSettings.TradeLevelSettings tradeLevelSettings, int level, RegistryAccess registryAccess) {
        TradeOfferHelper.registerVillagerOffers(WINEMAKER_KEY, level, factories -> {
            for (VineryFabricConfig.VillagerSettings.TradeEntry entry : tradeLevelSettings.trades) {
                // Validate price is within valid range (1-99)
                if (entry.price < 1 || entry.price > 99) {
                    System.err.println("Vinery Villager Trade has invalid price: " + entry.price + " (must be 1-99). Skipping trade for item: " + entry.item);
                    continue;
                }

                // Validate max uses is positive
                if (entry.maxUses < 1) {
                    System.err.println("Vinery Villager Trade has invalid maxUses: " + entry.maxUses + " (must be >= 1). Skipping trade for item: " + entry.item);
                    continue;
                }

                String[] parts = entry.item.split(":");
                if (parts.length >= 2) {
                    String modId = parts[0];
                    String itemId = parts[1];
                    ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(modId, itemId);
                    Item item = registryAccess.lookupOrThrow(Registries.ITEM).getValue(rl);

                    // Validate item exists and is not air
                    if (item != null && item != Items.AIR) {
                        if (entry.type == VineryFabricConfig.VillagerSettings.TradeType.BUY) {
                            factories.add(new VillagerUtil.BuyForOneEmeraldFactory(item, entry.price, entry.maxUses, entry.experience));
                        } else if (entry.type == VineryFabricConfig.VillagerSettings.TradeType.SELL) {
                            factories.add(new VillagerUtil.SellItemFactory(item, entry.price, entry.maxUses, entry.experience));
                        }
                    } else {
                        System.err.println("Vinery Villager Trade Item not found or is AIR: " + rl);
                    }
                } else {
                    System.err.println("Vinery Villager Trade has invalid item format: " + entry.item);
                }
            }
        });
    }
}
