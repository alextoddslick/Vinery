# Vinery port: Minecraft 26.2 -> 26.3 (Fabric only for now)

Same process and rules as `PORTING_NOTES_26.2.md` / `PORTING_NOTES_26.1.md` (read them first, especially the
"Verified during the port" sections). **Fabric is the priority.** NeoForge is DISABLED on this branch until NeoForge
publishes a 26.3 build (see "Toolchain").

## Status (2026-09-13)
- Branch `26.3` created from `26.2` head (`a3aa71a9`). Worktree: `.claude/worktrees/26.3` of the main repo.
- Toolchain bumped to 26.3-rc-2 (release candidate; bump `minecraft_version` and `fabric_api_version` again when 26.3 final ships).
- Gradle configures and resolves everything. `:common:compileJava` = **157 unique errors** (baseline log:
  `compile-errors-26.3-baseline.log`). Nothing ported yet.

## Base check (worktrees are sometimes created from the wrong commit)
```
grep minecraft_version gradle.properties    # MUST print minecraft_version=26.3-rc-2
```

## Build
```
export JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home CURSEFORGE_API_KEY=x
./gradlew :common:compileJava --continue -q 2>&1 | grep -E "error:|symbol:|location:"
```
Note: in a worktree session, write logs under `build/` (ignored), not the scratchpad (the path contains "Github" and the
worktree guard refuses commands naming git-ish paths).

## Toolchain (applied on the `26.3` base)
- Minecraft `26.3-rc-2`, Fabric API `0.160.4+26.3`, Fabric loader `0.19.5`, ModMenu `21.0.0-beta.1` (targets 26.3-rc-1).
- Still on their 26.2 builds because no 26.3 build exists yet (2026-09-13): Architectury `21.1.9`, REI `26.2.821`,
  cloth-config `26.2.155`, JEI `30.32.0.215`. JEI's artifact name embeds the MC version, so it now uses its own property
  `jei_minecraft_version=26.2` instead of `minecraft_version`. These compile-only/plain-Java deps may still fail at RUNTIME on
  26.3 (REI is `implementation` in `fabric/`, so the dedicated-server smoke test loads it) — bump them as they release.
- NeoForge: no 26.3 build. `include("neoforge")` is commented out in `settings.gradle` and `enabled_platforms=fabric`.
  Re-enable both once `net.neoforged:neoforge:26.3.*` exists.
- Unchanged: Gradle 9.5.1, loom-no-remap 1.17-SNAPSHOT, Java 25, AW `v2 official`.

## Baseline compile-error categories (157 unique, from the log above)
| Count | What broke | Where |
|---|---|---|
| ~50 | `ComposterBlock.COMPOSTABLES` gone | `core/registry/CompostableRegistry.java` (whole file) |
| ~25 | `BlockBehaviour.simpleCodec(...)` / `CODEC` gone (block codec pattern changed) | every custom block's `codec()` |
| ~40 | `method does not override` on blocks: `BonemealableBlock` now has `performBonemeal(ServerLevel, RandomSource, BlockPos, BlockState, BonemealSource)` and `isValidBonemealTarget`/`isBonemealSuccess` signatures changed; other overridden block methods lost/renamed | `AppleLeavesBlock`, `DarkCherryLeavesBlock`, `GrapeBush`, `GrapeVineBlock`, `LatticeBlock`, `PaleStemBlock`, `SpreadableGrassSlabBlock`, `StemBlock`, `ChairBlock`, `CabinetBlock`, `BigTableBlock`, `ApplePressBlock`, `FermentationBarrelBlock`, `GrapevinePotBlock`, `LineConnectingBlock`, `FacingBlock`, `DirtSlabBlock`, `DirtPathSlabBlock`, `StackableLogBlock`, `CompletionistBannerBlock`, `ChairRenderer` |
| 16 | `PoseStack.mulPose(Quaternionf)` gone; only `mulPose(Matrix4fc)` remains | `WineBoxRenderer`, `WineBottleRenderer`, `StorageBlockEntityRenderer`, `ShelfRenderer`, `LatticeRenderer`, `CompletionistBannerRenderer` |
| 12 | `net.minecraft.world.level.levelgen.feature.configurations` package gone; `Feature` no longer generic; `ConfiguredFeature`, `BlockStateConfiguration`, `FeaturePlaceContext`, `FeatureConfiguration` missing | `JungleGrapeFeature`, `VineryFeatures`, `VineryConfiguredFeatures` |
| 10 | `ShovelItem` / `AxeItem` classes gone (tool items are plain `Item` + component now?) | `ShovelItemMixin`, `GeneralUtil`, others |
| 5 | `LivingEntity.drop(...)` signature changed | `StorageBlock`, `WineBottleBlock`, `GrapeItem`, `GrapejuiceBottleItem`, `GeneralUtil` |
| 4 | `Level.playSound(Player, BlockPos, Holder<SoundEvent>, ...)` overload gone | `DirtSlabBlock`, `GrapeVineBlock`, `SpreadableGrassSlabBlock` |
| 3 | `isPathfindable`/similar now take a `Prediction` argument instead of `boolean` (`required: ItemStack,boolean,Prediction`) | `GrapevinePotBlock`, `DrinkBlockItem` (`updateCustomBlockEntityTag`) |
| 2 | `UntintedParticleLeavesBlock` constructor changed | `DarkCherryLeavesBlock` |
| 1 | `SubmitNodeCollector.submitModel(...)` signature changed | `CompletionistBannerRenderer` |
| 1 | `SignItem` missing | (see log) |
| 2 | `Block.DESTROY` / `IGNORE` constants (block update flags) moved | (see log) |
| 2 | `Registries.CONFIGURED_FEATURE` moved | `VineryConfiguredFeatures` |

Platform modules (`:fabric:compileJava`) have not been compiled yet; they only compile once `common` does.

## Reference sources
Not regenerated yet for 26.3. Follow "Regenerating the reference sources" in `HANDOFF.md` (decompile 26.3-rc-2 via loom's
`genSources` in this worktree, plus the vanilla data/assets jar) before porting; diff against the 26.2 sources.

## Verified during the port
(nothing yet — add API facts here as they are confirmed)

## Reference sources (regenerated 2026-09-13)
Base dir `S=/private/tmp/claude-501/-Users-alextodd-temp-Github-NOTSYNCED-Vinery/0be9815f-f3f4-4135-a2e2-0a80aae3a5d1/scratchpad`
- Minecraft 26.3-rc-2 decompiled: `$S/mc26.3/net/minecraft` (+ `$S/mc26.3/com/mojang` for blaze3d); 26.2 for diffing: `$S/mc26.2/net/minecraft`
- Vanilla 26.3-rc-2 data + assets: `$S/mcres26.3/{data,assets}/minecraft`; 26.2: `$S/mcres26.2/...`
- Fabric API 0.160.4+26.3 sources: `$S/ref263/fapi`. Everything else (Architectury 21.1.x, REI, JEI, cloth) is unchanged from 26.2: `$S/ref262/...`.
- Class move/remove/add table: `docs/porting/renames-26.2-to-26.3.txt` (11 moved, 131 removed, 346 new top-level classes).

## Verified API facts (26.2 -> 26.3-rc-2), from the decompiled source
- **Block codecs are gone.** No `MapCodec` / `simpleCodec` / `codec()` anywhere in `BlockBehaviour`/`Block` (`BlockTypes` deleted).
  Delete every `codec()` override and `CODEC` field in our blocks (and the `MapCodec` imports).
- **`BonemealableBlock`** (all three take a new `BonemealSource` enum `INTERACTION|MOB`):
  `isValidBonemealTarget(LevelReader, BlockPos, BlockState, BonemealSource)`,
  `isBonemealSuccess(Level, RandomSource, BlockPos, BlockState, BonemealSource)`,
  `performBonemeal(ServerLevel, RandomSource, BlockPos, BlockState, BonemealSource)`. `GrassBlock` implements the same.
- **`LivingEntity.drop(ItemStack, boolean thrownFromHand, Prediction)`** — `net.minecraft.util.Prediction` enum `PREDICTED|SERVER_ONLY`.
  Old 2-arg overload gone. Use `Prediction.SERVER_ONLY` when called from server-side logic (block use handlers), `PREDICTED` only where vanilla does client prediction.
- **`BlockItem.updateCustomBlockEntityTag(Level, @Nullable Player, BlockPos, ItemStack)`** (static; was `(BlockPos, Level, Player, ItemStack, BlockState)`).
- **`Level.playSound`** overloads now: `(Entity except, BlockPos, SoundEvent, SoundSource, float, float)`, `(Entity except, double x,y,z, SoundEvent, SoundSource[, float, float])`,
  `(Entity except, Entity source, SoundEvent|Holder<SoundEvent>, SoundSource, float, float)`. The `(Player, BlockPos, Holder<SoundEvent>, ...)` overload is gone:
  pass `SoundEvents.X` directly if it is a `SoundEvent`, or `.value()` on a `Holder<SoundEvent>`.
- **Tool item classes are gone**: `AxeItem`, `ShovelItem`, `HoeItem`, `SignItem`, `BedItem` deleted. Tools are plain `Item`s built with
  `Item.Properties().shovel(ToolMaterial, dmg, speed)` / `.axe(...)` etc. Replace `instanceof ShovelItem/AxeItem` checks with item tags
  (`ItemTags.SHOVELS` / `ItemTags.AXES` — verify names in `$S/mc26.3/net/minecraft/tags/ItemTags.java`) or the tool component.
  Signs: see how `Items.OAK_SIGN` is built in `$S/mc26.3/net/minecraft/world/item/Items.java` (`Item.Properties().signText()` exists) and mirror it for `DARK_CHERRY_SIGN_ITEM`.
  Shovel path-flattening moved out of `ShovelItem`: find where `FLATTENABLES`/dirt-path conversion now lives (grep `PathBlock`, `flatten` in `$S/mc26.3`) and retarget `ShovelItemMixin`
  (or drop it if the new code path already skips single slabs).
- **`ComposterBlock.COMPOSTABLES` is gone.** Compostability is the item data component `DataComponents.COMPOSTABLE` (`Compostable(ResolvableInt layers)`),
  set via `Item.Properties().compostable(ContextIntProviders.COMPOSTABLE_LOW)` (keys in `$S/mc26.3/net/minecraft/world/level/storage/loot/providers/number/ints/ContextIntProviders.java`;
  pick the key whose vanilla value matches 0.4 -> check `$S/mcres26.3/data/minecraft/context_int_provider/compostable/*.json` or wherever those live). Fabric API 0.160 has no `CompostingChanceRegistry` any more.
- **`PoseStack.mulPose(Quaternionf)` is gone** -> `rotate(Quaternionfc)`, `rotate(Axis, float radians)`, `rotateDegrees(Axis, float)`, `rotateAround(...)`; `mulPose(Matrix4fc)` / `mulPose(Transformation)` remain.
- **`OrderedSubmitNodeCollector.submitModel(...)`** signatures changed (4 overloads at `$S/mc26.3/net/minecraft/client/renderer/OrderedSubmitNodeCollector.java:66-110`). Read them and adapt `CompletionistBannerRenderer`.
- **Leaves**: `UntintedParticleLeavesBlock(...)` and `LeavesBlock(AmbientLeavesBlockSoundPlayer, Properties)` constructors changed — read `$S/mc26.3/net/minecraft/world/level/block/{LeavesBlock,UntintedParticleLeavesBlock,TintedParticleLeavesBlock}.java`.
- **`DirtPathBlock` deleted** -> `PathBlock(Block base, Properties)` (see `Blocks.DIRT_PATH`). Affects `DirtPathSlabBlock`.
- **`Block.DESTROY` / `Block.IGNORE`**: only the `UPDATE_*` int flags exist now (`UPDATE_NEIGHBORS, UPDATE_CLIENTS, UPDATE_INVISIBLE, UPDATE_IMMEDIATE, UPDATE_KNOWN_SHAPE, UPDATE_SUPPRESS_DROPS, UPDATE_MOVE_BY_PISTON, ..., UPDATE_NONE=260, UPDATE_ALL=3`).
- **Worldgen rewritten.** `ConfiguredFeature`, `FeaturePlaceContext`, `FeatureConfiguration` and the whole `feature/configurations` package are gone.
  `Feature` is now an **interface**: `MapCodec<? extends Feature> codec()` + `boolean place(WorldGenLevel, ChunkGenerator, RandomSource, BlockPos origin)`.
  A feature is a record holding its own config (e.g. `SimpleBlockFeature(Holder<BlockStateProvider> toPlace, boolean scheduleTick)`), its `MapCodec` is registered in
  `Registries.FEATURE_TYPE` (`worldgen/feature_type`, `Registry<MapCodec<? extends Feature>>`; vanilla does it in `FeatureTypes`), and `Registries.FEATURE`
  (`worldgen/feature`) is now the *datapack* registry that used to be `CONFIGURED_FEATURE` (which no longer exists). `Registries.PLACED_FEATURE` unchanged.
  Data: folder `data/<ns>/worldgen/configured_feature/` -> `data/<ns>/worldgen/feature/` and the JSON is FLAT (no `"config": {}` wrapper; the type's fields sit next to `"type"`).
  `configured_carver` -> `carver`. New folders `block_state_provider`, `material_condition`, `material_rule`.
- **Block state JSON keys** changed everywhere codecs use `BlockState`: `{"Name": ..., "Properties": {...}}` -> `{"id": ..., "properties": {...}}` (`StateHolder.ID_TAG/PROPERTIES_TAG`).
  Block state providers accept either a bare block state or a typed provider (`BlockStateProvider.STATE_OR_PROVIDER_CODEC`), so `"to_place": {"type":"minecraft:simple_state_provider","state":{...}}`
  can become `"to_place": {"id": "...", "properties": {...}}` (compare `$S/mcres26.2` vs `$S/mcres26.3` `worldgen/feature/*.json`).
- **Placement modifier** `minecraft:random_offset` (`xz_spread`/`y_spread`) -> `minecraft:offset` with `x`/`y`/`z` (see `placed_feature/patch_berry_common.json` in both versions).
- **Loot tables**: `providers/number/{ConstantValue,UniformGenerator,NumberProvider,NumberProviders,...}` deleted, replaced by `providers/number/ints/*` and `floats/*`;
  `LootItemBlockStatePropertyCondition`, `LootItemConditions`, `IntRange`, `LootPoolSingletonContainer` deleted. Expect JSON changes in our loot tables — diff a vanilla
  crop/bush loot table (`data/minecraft/loot_table/blocks/sweet_berry_bush.json`, `wheat.json`) between `$S/mcres26.2` and `$S/mcres26.3` before touching ours.
- Pack formats 26.3-rc-2: resource `97.1`, data `121.0` (26.2 was 88 / 107.1). `bushy_leaves/pack.mcmeta` already bumped to 97.
- Fabric loader 0.19.5; Fabric API 0.160.4+26.3 module list: attachment biome block blockgetter client command creativetab datagen debug dimension entity event gamerule gametest item lookup loot menu message networking object particle permission recipe registry resource serialization tag transfer util.
- Architectury 21.1.9 is a 26.2 build. `AxeItemHooks` / `ShovelItemHooks` (used in `Vinery.commonSetup`) may reference deleted classes at runtime; the dedicated-server smoke test will tell.
