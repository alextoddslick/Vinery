package net.satisfy.vinery.neoforge.core.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.registry.ObjectRegistry;

public class VineryNeoForgeVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Vinery.MOD_ID);

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, Vinery.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> WINEMAKER_POI = POI_TYPES.register("winemaker_poi", () ->
            new PoiType(ImmutableSet.copyOf(ObjectRegistry.FERMENTATION_BARREL.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> WINEMAKER = VILLAGER_PROFESSIONS.register("winemaker", () ->
            new VillagerProfession(
                    Component.translatable("entity." + Vinery.MOD_ID + ".villager.winemaker"),
                    holder -> holder.is(WINEMAKER_POI.getKey()),
                    holder -> holder.is(WINEMAKER_POI.getKey()),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_FARMER));

    public static ResourceKey<VillagerProfession> winemakerKey() {
        return WINEMAKER.getKey();
    }

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
