package net.satisfy.vinery.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.vinery.client.gui.handler.ApplePressGuiHandler;
import net.satisfy.vinery.core.Vinery;

@Environment(EnvType.CLIENT)
public class ApplePressGui extends AbstractContainerScreen<ApplePressGuiHandler> {
    public static final Identifier TEXTURE = Vinery.identifier("textures/gui/apple_press_gui.png");

    public static final int MASHING_BAR_X = 40;
    public static final int MASHING_BAR_Y = 17;
    public static final int MASHING_BAR_WIDTH = 24;
    public static final int MASHING_BAR_HEIGHT = 38;
    public static final int MASHING_BAR_U = 176;
    public static final int MASHING_BAR_V = 0;

    public static final int FERMENTING_BAR_X = 101;
    public static final int FERMENTING_BAR_Y = 18;
    public static final int FERMENTING_BAR_WIDTH = 10;
    public static final int FERMENTING_BAR_HEIGHT = 28;
    public static final int FERMENTING_BAR_U = 176;
    public static final int FERMENTING_BAR_V = 47;

    public ApplePressGui(ApplePressGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    /**
     * Since 26.1 screens no longer have {@code render}/{@code renderBg}: the background texture is contributed to
     * the GUI render state from {@code extractBackground} (see vanilla {@code AbstractFurnaceScreen}), and the
     * slots, labels and tooltips are handled by {@code AbstractContainerScreen.extractRenderState}.
     */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        this.extractProgressArrows(graphics, x, y);
    }

    private void extractProgressArrows(GuiGraphicsExtractor graphics, int x, int y) {
        if (menu.isCrafting(0)) {
            int height = menu.getScaledProgress(0);
            int xPosition = x + MASHING_BAR_X;
            int yPosition = y + MASHING_BAR_Y + height;
            int textureV = MASHING_BAR_V + height;
            int renderHeight = MASHING_BAR_HEIGHT - height;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xPosition, yPosition, MASHING_BAR_U, textureV, MASHING_BAR_WIDTH, renderHeight, 256, 256);
        }
        if (menu.isCrafting(1)) {
            int height = menu.getScaledProgress(1);
            int xPosition = x + FERMENTING_BAR_X;
            int yPosition = y + FERMENTING_BAR_Y + FERMENTING_BAR_HEIGHT - height;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xPosition, yPosition, FERMENTING_BAR_U, FERMENTING_BAR_V + FERMENTING_BAR_HEIGHT - height, FERMENTING_BAR_WIDTH, height, 256, 256);
        }
    }
}
