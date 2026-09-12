package net.satisfy.vinery.client.util;

import net.minecraft.client.Minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class ClientUtil {
    public static int getLightLevel(Level world, BlockPos pos) {
        int bLight = world.getBrightness(LightLayer.BLOCK, pos);
        int sLight = world.getBrightness(LightLayer.SKY, pos);
        return LightCoordsUtil.pack(bLight, sLight);
    }

    /** Client-only accessor; never call from code that can run on a dedicated server. */
    public static Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    public static boolean hasClientLevel() {
        return Minecraft.getInstance().level != null;
    }
}
