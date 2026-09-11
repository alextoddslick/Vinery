package net.satisfy.vinery.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Mth;

/**
 * The waving flag of the Vinery completionist banner. Kept as a {@link Model} so that the wave animation is
 * applied on the render thread (in {@link #setupAnim}) instead of mutating a shared {@link ModelPart} during
 * render state extraction.
 */
public class CompletionistBannerFlagModel extends Model<Float> {
    public static final String FLAG = "flag";

    private final ModelPart flag;

    public CompletionistBannerFlagModel(ModelPart root) {
        super(root, RenderType::entitySolid);
        this.flag = root.getChild(FLAG);
    }

    public static LayerDefinition createFlagLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild(FLAG, CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, -1.0F, 20.0F, 40.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -44.0F, -1.0F, -0.0349F, 0.0F, 0.0F));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(Float phase) {
        super.setupAnim(phase);
        this.flag.xRot = (-0.0125F + 0.01F * Mth.cos((float) Math.PI * 2 * phase)) * (float) Math.PI;
        this.flag.y = -32.0F;
    }
}
