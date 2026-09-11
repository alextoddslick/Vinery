package net.satisfy.vinery.client.render.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class CompletionistBannerRenderState extends BlockEntityRenderState {
    /** Texture of the flag, taken from the banner block. */
    @Nullable
    public Identifier texture;
    /** Y rotation of the whole banner in degrees. */
    public float angle;
    /** {@code true} for the standing variant (pole visible), {@code false} for the wall variant. */
    public boolean standing = true;
    /** Wave phase of the flag, 0..1. */
    public float phase;
}
