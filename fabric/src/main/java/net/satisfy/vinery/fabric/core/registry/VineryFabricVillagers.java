package net.satisfy.vinery.fabric.core.registry;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.util.VillagerUtil;

public class VineryFabricVillagers {

    private static final Identifier WINEMAKER_POI_IDENTIFIER = Vinery.identifier("winemaker_poi");
    private static final Identifier WINEMAKER_IDENTIFIER = Vinery.identifier("winemaker");

    public static final ResourceKey<PoiType> WINEMAKER_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, WINEMAKER_POI_IDENTIFIER);
    public static final ResourceKey<VillagerProfession> WINEMAKER_KEY = ResourceKey.create(Registries.VILLAGER_PROFESSION, WINEMAKER_IDENTIFIER);

    public static final PoiType WINEMAKER_POI;
    public static final VillagerProfession WINEMAKER;

    static {
        WINEMAKER_POI = PoiHelper.register(
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
                        SoundEvents.VILLAGER_WORK_FARMER,
                        VillagerUtil.winemakerTradeSets()
                )
        );
    }

    public static void registerPOIAndProfession() {
    }
}
