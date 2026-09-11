package net.satisfy.vinery.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.registry.CompostableRegistry;
import net.satisfy.vinery.core.util.PreInit;
import net.satisfy.vinery.neoforge.core.config.VineryForgeConfig;
import net.satisfy.vinery.neoforge.core.registry.VineryNeoForgeVillagers;

@Mod(Vinery.MOD_ID)
public class VineryForge {
    public VineryForge(IEventBus modEventBus, ModContainer modContainer) {
        PreInit.preInit();
        Vinery.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, VineryForgeConfig.COMMON_CONFIG, "vinery.toml");

        modEventBus.register(VineryForgeConfig.class);

        VineryNeoForgeVillagers.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CompostableRegistry.registerCompostable();
            Vinery.commonSetup();
        });
    }
}
