package net.satisfy.vinery.core.block.state.properties;

import net.minecraft.world.level.block.state.properties.Property;
import net.satisfy.vinery.core.registry.GrapeTypeRegistry;
import net.satisfy.vinery.core.util.GrapeType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GrapeProperty extends Property<GrapeType> {
    private final List<GrapeType> values;
    private final Map<String, GrapeType> byName;
    private final Map<GrapeType, Integer> indexByValue;

    protected GrapeProperty(String name) {
        super(name, GrapeType.class);
        this.values = GrapeTypeRegistry.GRAPE_TYPE_TYPES.stream().sorted().toList();
        Map<String, GrapeType> byName = new HashMap<>();
        Map<GrapeType, Integer> indexByValue = new HashMap<>();
        for (int i = 0; i < this.values.size(); i++) {
            GrapeType grapeType = this.values.get(i);
            byName.put(grapeType.getSerializedName(), grapeType);
            indexByValue.put(grapeType, i);
        }
        this.byName = Map.copyOf(byName);
        this.indexByValue = Map.copyOf(indexByValue);
    }

    public static GrapeProperty create(String name) {
        return new GrapeProperty(name);
    }

    @Override
    public @NotNull List<GrapeType> getPossibleValues() {
        return this.values;
    }

    @Override
    public @NotNull String getName(GrapeType grapeType) {
        return grapeType.getSerializedName();
    }

    @Override
    public @NotNull Optional<GrapeType> getValue(String string) {
        return Optional.ofNullable(this.byName.get(string));
    }

    @Override
    public int getInternalIndex(GrapeType grapeType) {
        Integer index = this.indexByValue.get(grapeType);
        return index == null ? -1 : index;
    }
}
