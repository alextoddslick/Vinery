package net.satisfy.vinery.fabric.core.rei;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.satisfy.vinery.core.compat.rei.VineryReiCommonPlugin;

public class VineryReiCommonPluginFabric implements REICommonPlugin {
    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        VineryReiCommonPlugin.registerDisplays(registry);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        VineryReiCommonPlugin.registerDisplaySerializer(registry);
    }
}
