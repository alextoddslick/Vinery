package net.satisfy.vinery.neoforge.core.rei;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.forge.REIPluginCommon;
import net.satisfy.vinery.core.compat.rei.VineryReiCommonPlugin;

@REIPluginCommon
public class VineryReiCommonPluginForge implements REICommonPlugin {
    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        VineryReiCommonPlugin.registerDisplays(registry);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        VineryReiCommonPlugin.registerDisplaySerializer(registry);
    }
}
