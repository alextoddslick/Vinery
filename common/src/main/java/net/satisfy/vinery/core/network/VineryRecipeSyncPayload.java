package net.satisfy.vinery.core.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.vinery.core.Vinery;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Ships every Vinery recipe to the client, because since 1.21.5 the vanilla recipe book no longer
 * synchronises full recipes and recipe viewers (JEI in particular) have nothing to read on the client.
 */
public record VineryRecipeSyncPayload(List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VineryRecipeSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Vinery.identifier("recipe_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, VineryRecipeSyncPayload> STREAM_CODEC =
            RecipeHolder.STREAM_CODEC.<List<RecipeHolder<?>>>apply(ByteBufCodecs.list())
                    .map(VineryRecipeSyncPayload::new, VineryRecipeSyncPayload::recipes);

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
