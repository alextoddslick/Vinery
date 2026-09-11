package net.satisfy.vinery.core.network;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client side mirror of the Vinery recipes owned by the server, filled by {@link VineryRecipeSyncPayload}.
 * Recipe viewers read their recipes from here instead of from the (now recipe-less) client recipe manager.
 */
public final class VineryClientRecipeCache {
    private static final Map<RecipeType<?>, List<RecipeHolder<?>>> BY_TYPE = new HashMap<>();
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();

    private VineryClientRecipeCache() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> List<RecipeHolder<T>> get(RecipeType<T> type) {
        synchronized (BY_TYPE) {
            List<RecipeHolder<?>> holders = BY_TYPE.get(type);
            if (holders == null || holders.isEmpty()) return List.of();
            return (List<RecipeHolder<T>>) (List<?>) List.copyOf(holders);
        }
    }

    /**
     * Replaces the whole cache with the recipes of the packet and notifies every listener.
     */
    public static void accept(List<RecipeHolder<?>> holders) {
        synchronized (BY_TYPE) {
            BY_TYPE.clear();
            for (RecipeHolder<?> holder : holders) {
                BY_TYPE.computeIfAbsent(holder.value().getType(), type -> new ArrayList<>()).add(holder);
            }
        }
        for (Runnable listener : LISTENERS) {
            listener.run();
        }
    }

    public static void clear() {
        synchronized (BY_TYPE) {
            BY_TYPE.clear();
        }
    }

    public static Map<RecipeType<?>, List<RecipeHolder<?>>> getAll() {
        synchronized (BY_TYPE) {
            return Collections.unmodifiableMap(new HashMap<>(BY_TYPE));
        }
    }

    /**
     * Registers a callback fired every time a sync packet has been applied.
     */
    public static void addUpdateListener(Runnable listener) {
        LISTENERS.add(listener);
    }
}
