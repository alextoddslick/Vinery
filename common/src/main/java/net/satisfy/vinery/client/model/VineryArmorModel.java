package net.satisfy.vinery.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import java.util.Set;

/**
 * Base class for Vinery's custom armor models.
 *
 * <p>Since 1.21.9 armor models are submitted as whole {@link net.minecraft.client.model.Model}s and animated by
 * {@link HumanoidModel#setupAnim} on the render thread, so instead of copying transforms from the wearer's model
 * (the old {@code copyHead}/{@code copyLegs}/{@code copyBody} calls) every model is now a full humanoid whose
 * unused parts are simply empty. The uniform scale/offset the models used to apply inside {@code renderToBuffer}
 * is baked into the root part instead.</p>
 */
public abstract class VineryArmorModel extends HumanoidModel<HumanoidRenderState> {
    protected VineryArmorModel(ModelPart root) {
        super(root);
    }

    /**
     * A standard humanoid skeleton (head + hat, body, arms, legs with vanilla part poses) with no cubes at all.
     */
    protected static MeshDefinition emptyHumanoidMesh() {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        meshDefinition.getRoot().retainExactParts(Set.of());
        return meshDefinition;
    }

    /** Uniform scale that used to be applied via {@code poseStack.scale(..)} before rendering. */
    protected abstract float armorScale();

    /** Vertical offset in pixels applied to the root part, already compensated for {@link #armorScale()}. */
    protected float armorYOffset() {
        return 0.0F;
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        super.setupAnim(state);
        ModelPart root = this.root();
        float scale = this.armorScale();
        root.xScale = scale;
        root.yScale = scale;
        root.zScale = scale;
        root.y = this.armorYOffset();
    }
}
