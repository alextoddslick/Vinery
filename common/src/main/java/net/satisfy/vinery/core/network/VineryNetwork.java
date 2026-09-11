package net.satisfy.vinery.core.network;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Recipe synchronisation for the recipe viewers. Clients no longer receive full recipes from vanilla,
 * so Vinery ships its own three recipe types to every player on join and after every datapack reload.
 */
public class VineryNetwork {

    /**
     * The recipe manager instance of the last sync. {@code /reload} builds a brand new
     * {@link RecipeManager}, so an identity change is a reliable, loader agnostic reload signal.
     */
    @Nullable
    private static RecipeManager lastSyncedRecipeManager;

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, VineryRecipeSyncPayload.TYPE, VineryRecipeSyncPayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> VineryClientRecipeCache.accept(payload.recipes())));

        PlayerEvent.PLAYER_JOIN.register(VineryNetwork::onPlayerJoin);
        TickEvent.SERVER_POST.register(VineryNetwork::onServerTick);
        LifecycleEvent.SERVER_STOPPED.register(server -> lastSyncedRecipeManager = null);
    }

    private static void onPlayerJoin(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        RecipeManager manager = server.getRecipeManager();
        lastSyncedRecipeManager = manager;
        sendTo(player, collect(manager));
    }

    private static void onServerTick(MinecraftServer server) {
        RecipeManager manager = server.getRecipeManager();
        if (manager == lastSyncedRecipeManager) return;
        lastSyncedRecipeManager = manager;

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) return;
        VineryRecipeSyncPayload payload = collect(manager);
        for (ServerPlayer player : players) {
            sendTo(player, payload);
        }
    }

    private static void sendTo(ServerPlayer player, VineryRecipeSyncPayload payload) {
        if (!NetworkManager.canPlayerReceive(player, VineryRecipeSyncPayload.TYPE)) return;
        NetworkManager.sendToPlayer(player, payload);
    }

    private static VineryRecipeSyncPayload collect(RecipeManager manager) {
        Set<RecipeType<?>> types = Set.of(
                RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get(),
                RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_TYPE.get(),
                RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_TYPE.get()
        );
        List<RecipeHolder<?>> holders = new ArrayList<>();
        for (RecipeHolder<?> holder : manager.getRecipes()) {
            if (types.contains(holder.value().getType())) {
                holders.add(holder);
            }
        }
        return new VineryRecipeSyncPayload(holders);
    }
}
