package net.satisfy.vinery.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.vinery.core.Vinery;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class WanderingWinemakerRenderer extends WanderingTraderRenderer {
	private static final ResourceLocation TEXTURE = Vinery.identifier("textures/entity/wandering_winemaker.png");

	public WanderingWinemakerRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull VillagerRenderState state) {
		return TEXTURE;
	}
}
