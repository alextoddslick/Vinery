package net.satisfy.vinery.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.satisfy.vinery.client.model.MuleModel;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.entity.TraderMuleEntity;
import org.jetbrains.annotations.NotNull;

@Environment(value = EnvType.CLIENT)
public class MuleRenderer extends MobRenderer<TraderMuleEntity, EquineRenderState, MuleModel> {
    private static final Identifier TEXTURE = Vinery.identifier("textures/entity/wandering_mule.png");

    public MuleRenderer(EntityRendererProvider.Context context) {
        super(context, new MuleModel(context.bakeLayer(MuleModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull EquineRenderState state) {
        return TEXTURE;
    }

    @Override
    public @NotNull EquineRenderState createRenderState() {
        return new EquineRenderState();
    }

    @Override
    public void extractRenderState(TraderMuleEntity entity, EquineRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.saddle = entity.getItemBySlot(EquipmentSlot.SADDLE).copy();
        state.isRidden = entity.isVehicle();
        state.eatAnimation = entity.getEatAnim(partialTick);
        state.standAnimation = entity.getStandAnim(partialTick);
        state.feedingAnimation = entity.getMouthAnim(partialTick);
        state.animateTail = entity.tailCounter > 0;
    }
}
