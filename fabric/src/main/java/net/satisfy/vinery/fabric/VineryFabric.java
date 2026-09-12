package net.satisfy.vinery.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.Identifier;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.registry.CompostableRegistry;
import net.satisfy.vinery.fabric.config.VineryFabricConfig;
import net.satisfy.vinery.fabric.core.registry.VineryFabricVillagers;
import net.satisfy.vinery.fabric.core.world.VineryBiomeModification;

import java.util.Optional;

public class VineryFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AutoConfig.register(VineryFabricConfig.class, GsonConfigSerializer::new);

        Vinery.init();
        // must run after Vinery.init(), the POI type is built from the (now registered) fermentation barrel block
        VineryFabricVillagers.registerPOIAndProfession();
        CompostableRegistry.registerCompostable();
        VineryBiomeModification.init();
        Vinery.commonSetup();


        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Vinery.MOD_ID);
        modContainer.ifPresent(container -> ResourceLoader.registerBuiltinPack(
                Identifier.fromNamespaceAndPath(Vinery.MOD_ID, "bushy_leaves"),
                container,
                PackActivationType.NORMAL
        ));
    }
}
