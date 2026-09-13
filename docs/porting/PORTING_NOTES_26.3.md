# Vinery port: Minecraft 26.2 -> 26.3 (Fabric only for now)

Same process and rules as `PORTING_NOTES_26.2.md` / `PORTING_NOTES_26.1.md` (read them first, especially the
"Verified during the port" sections). **Fabric is the priority.** NeoForge is DISABLED on this branch until NeoForge
publishes a 26.3 build (see "Toolchain").

## Status (2026-09-13, evening) — DONE for Fabric
- Branch `26.3` (from `26.2` head `a3aa71a9`), worktree `.claude/worktrees/26.3`. Toolchain 26.3-rc-2 (bump `minecraft_version`
  and `fabric_api_version` again when 26.3 final ships).
- Baseline was 157 compile errors (`compile-errors-26.3-baseline.log`); now `./gradlew build` passes and `:fabric:runServer` boots to
  `Done` with zero errors. Not client-tested. Temporary toolchain workarounds are listed under "Runtime / toolchain findings":
  local Architectury 22.0.9999, REI compile-only, LWJGL excluded from the transformer classpath, NeoForge module disabled.

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
- (client renderers, 26.3-client) `PoseStack.mulPose(Quaternionf)` -> `PoseStack.rotate(Quaternionfc)`; `Axis.YP.rotationDegrees(f)` still returns a `Quaternionf`, so `poseStack.rotate(Axis.YP.rotationDegrees(f))` is the drop-in replacement (applied in all storage renderers, `LatticeRenderer`, `CompletionistBannerRenderer`).
- (client renderers) `OrderedSubmitNodeCollector.submitModel` lost its trailing `@Nullable CrumblingOverlay` parameter in every overload; the 8-arg `(model, state, pose, renderType, light, overlay, outlineColor, crumblingOverlay)` form is now the 7-arg `(model, state, pose, renderType, light, overlay, outlineColor)`. The full overload is `(model, state, pose, renderType, light, overlay, tintedColor, @Nullable UvMapping, outlineColor)` (`TextureAtlasSprite` implements `UvMapping`). Break progress is submitted separately via `submitCrumblingOverlay(model, state, pose, renderType, light, overlay, tintedColor, crumblingOverlay)` (vanilla `BannerRenderer.submitBanner` does `if (breakProgress != null) collector.order(n).submitCrumblingOverlay(...)`). Fabric `ArmorRenderer.render` signature is unchanged in 0.160.4, but our armor renderers passed the old trailing `null` and had to drop it.
- (client renderers) `EntityRenderer.shouldRender(T, Frustum, double camX, double camY, double camZ, float partialTicks)` gained the `partialTicks` parameter (`ChairRenderer`).
- (client, verified for `VineryClientFabric`) `Entity.needsSync` (public field), `LivingEntity.canGlideUsing(ItemStack, EquipmentSlot)`, `LivingEntity.isFallFlying()`, `Options.keyJump`, `MobEffects.LEVITATION` (`Holder<MobEffect>`) and Fabric `ClientTickEvents.END_CLIENT_TICK` are unchanged.
- (blocks) `BonemealSource` lives in `net.minecraft.world.level.block` (same package as `BonemealableBlock`); import it explicitly.
  When delegating to a vanilla bonemealable (e.g. `GrassBlock.performBonemeal`), pass the `source` you received through.
- (blocks) `TintedParticleLeavesBlock(float, Properties)` is unchanged. `UntintedParticleLeavesBlock(float, ParticleOptions, AmbientLeavesBlockSoundPlayer, Properties)`:
  pass `AmbientLeavesBlockSoundPlayer.noAmbientSound()` (package `net.minecraft.world.level.block.sounds`) to keep 26.2 behaviour; vanilla cherry/pale-oak leaves do the same.
- (blocks) `SoundEvents.SHOVEL_FLATTEN` and `SoundEvents.AXE_STRIP` are `Holder.Reference<SoundEvent>` -> `.value()` for `Level.playSound(Entity, BlockPos, SoundEvent, ...)`.
  `SWEET_BERRY_BUSH_*`, `ITEM_FRAME_REMOVE_ITEM`, `FLINTANDSTEEL_USE`, `GENERIC_EXTINGUISH_FIRE` are plain `SoundEvent`s (no change).
- (blocks) `LivingEntity.drop(ItemStack, boolean thrownFromHand, Prediction)`: the old 3-arg `Player.drop(ItemStack, boolean dropAround, boolean thrownFromHand)` is gone too.
  Vanilla precedent: `ChiseledBookShelfBlock` uses `Prediction.SERVER_ONLY` inside a `!isClientSide()` branch; `FlowerPotBlock`/`BeehiveBlock` use `Prediction.PREDICTED`
  in unguarded use handlers that run on both sides. `Prediction` only affects the swing animation (`swing(hand, DEFAULT, prediction != PREDICTED)`); the item entity is only spawned server-side.
- (blocks) `ItemTags.AXES / SHOVELS / HOES / PICKAXES` confirmed at `tags/ItemTags.java:176-179`; `stack.is(ItemTags.AXES)` replaces `instanceof AxeItem`.
- (blocks) `DirtPathSlabBlock` never referenced `DirtPathBlock` (it extends `SlabBlock` with its own path logic), so only its codec had to go. `PathBlock`'s constructor is `protected PathBlock(Block baseBlock, Properties)`.
- (blocks) Removing `codec()` overrides can leave `HorizontalDirectionalBlock` / `BaseEntityBlock` / `NotNull` imports unused (they were only referenced in the `MapCodec<? extends X>` return type).
- (core) **Compostable**: `Item.Properties.compostable(ResourceKey<ContextIntProvider>)` sets `DataComponents.COMPOSTABLE`. Vanilla keys (`ContextIntProviders`, values from
  `bootstrap`): `COMPOSTABLE_LOW` = 30 %, `COMPOSTABLE_LOW_MEDIUM` = 50 %, `COMPOSTABLE_MEDIUM` = 65 %, `COMPOSTABLE_MEDIUM_HIGH` = 85 %, `COMPOSTABLE_ALWAYS_ADD_ONE` = 100 %
  (vanilla: seeds/leaves/saplings LOW, apple/flowers MEDIUM). Vinery's old flat 0.4 -> `COMPOSTABLE_LOW_MEDIUM` for every item that was in `CompostableRegistry`
  (block items go through `ObjectRegistry.registerWithCompostableItem`). `CompostableRegistry` deleted; Fabric API 0.160 has no `CompostingChanceRegistry`.
- (core) **Stripping / flattening / tilling are data now**: datapack registry `Registries.BLOCK_TRANSFORMER` (`data/minecraft/block_transformer/{axe,shovel,hoe}.json`,
  `net.minecraft.core.component.BlockTransformer`), attached to tool items as `DataComponents.BLOCK_TRANSFORMER`; `Item.useOn` calls `BlockTransformer.transformBlock(UseOnContext)`.
  Vanilla shovel data = tag `minecraft:turns_into_dirt_path` + air above -> `dirt_path`. Architectury 21.1.9 `AxeItemHooks`/`ShovelItemHooks` touch the deleted
  `AxeItem.STRIPPABLES`/`ShovelItem.FLATTENABLES` (NoClassDefFoundError at runtime) -> replaced by `PlatformHelper.registerStrippable/registerFlattenable` (`@ExpectPlatform`);
  Fabric impl uses `net.fabricmc.fabric.api.item.v1.BlockTransformerHelper.registerStripping(Block, Block)` / `registerFlattening(Block, BlockState)` (fabric-item-api-v1;
  it appends transforms to the vanilla axe/shovel transformer when the registry loads; call during mod init). Stripping copies properties (`CopyPropertiesProvider`), flattening
  requires air above like vanilla. `ShovelItemMixin` (single slabs never flatten) is now `BlockTransformerMixin` on `BlockTransformer.transformBlock` HEAD, gated on `ItemTags.SHOVELS`.
  `BlockPredicate` has no state-property matcher, so the slab guard cannot be expressed in data.
- (core) **Signs**: `new StandingAndWallBlockItem(sign, wallSign, Direction.DOWN, props.signText())` replaces `SignItem`; `HangingSignItem(sign, wallSign, props)` unchanged but vanilla
  adds `.signText()` to its properties too.
- (core) **`PushReaction`** renamed: `NORMAL -> PUSH_PULL`, `DESTROY -> POPPED`, `BLOCK -> IMMOVEABLE`, `IGNORE -> IGNORE_ENTITY`, `PUSH_ONLY -> PUSH`.
- (core) **`TreeGrower`** has one constructor: `(String name, WeightedList<ResourceKey<Feature>> trees, WeightedList<ResourceKey<Feature>> megaTrees, WeightedList<ResourceKey<Feature>> flowerTrees,
  @Nullable ResourceKey<Feature> shortestTreeType)` (`net.minecraft.util.random.WeightedList.of(...)`). The old 4-arg `(name, megaTree, tree, flowers)` order was mega FIRST, so
  `apple_tree` = trees `apple_variant`, megaTrees `apple`. `shortestTreeType` only feeds `getMinimumHeight` (`TreeFeature.trunkPlacer().getBaseHeight()`).
- (core) **Worldgen**: `Registries.FEATURE_TYPE` is `Registry<MapCodec<? extends Feature>>` -> Architectury `DeferredRegister.create(MOD_ID, Registries.FEATURE_TYPE).getRegistrar()`
  typed `Registrar<MapCodec<? extends Feature>>` works; register the record's `CODEC`, not a Feature instance. Our feature: `record JungleGrapeFeature(BlockState state) implements Feature`,
  JSON `{"type":"vinery:jungle_grape_feature","state":{"id":"vinery:jungle_grape_bush_red","properties":{...}}}` (`BlockState.CODEC`, bare id allowed). `TreeGrower`/`SaplingBlock` keys are `ResourceKey<Feature>` in `Registries.FEATURE`.
  Fabric `BiomeModifications` / `BiomeSelectors.tag` / `GenerationSettingsContext.addFeature(Decoration, ResourceKey<PlacedFeature>)` unchanged in 0.160.4.
- (core) **`BlockItem.updateCustomBlockEntityTag(Level, @Nullable Player, BlockPos, ItemStack)` is `public static`** -> cannot be overridden. `place()` order is
  `placeBlock(context, state)` -> `updateBlockStateFromTag` -> `updateCustomBlockEntityTag` -> `updateBlockEntityComponents` -> `setPlacedBy`; override `protected boolean placeBlock(BlockPlaceContext, BlockState)`
  and act after `super.placeBlock(...)` returns true (`DrinkBlockItem`).
- (core) Unchanged in 26.3 / Fabric 0.160.4 (verified for the fabric glue): `PoiHelper.register(Identifier, int, int, Block...)`, `VillagerProfession` record (7 components incl.
  `Int2ObjectMap<ResourceKey<TradeSet>>`), `ResourceLoader.registerBuiltinPack(Identifier, ModContainer, PackActivationType)`, `ExperienceOrb.playerTouch` still calls `Player.giveExperiencePoints(I)V`.

- (resources) **Loot tables** (verified against `LootTable`/`LootPool`/`LootPoolEntryContainer`/`LootItemConditionalFunction` codecs + vanilla 26.3 files):
  `"conditions": [..]` -> `"condition": <one>` and `"functions": [..]` -> `"modifier": <one or list>` on tables, pools, entries and functions
  (a list under `modifier` is the inline `SequenceFunction`; a list under `condition` is NOT accepted -> wrap several in `{"type":"minecraft:all_of","terms":[..]}`).
  Conditions/functions are keyed by `"type"` (was `"condition"` / `"function"`). Conditions and functions are `Holder`s, so a bare string is a reference into
  `data/<ns>/predicate/` / `item_modifier/`: vanilla now uses `"minecraft:tool/can_shear"` and `"minecraft:tool/can_silk_touch"` instead of inline `match_tool`.
  `block_state_property {block, properties}` -> `match_block {blocks, state}` (`BlockPredicate.MAP_CODEC`; `blocks` accepts an id, `#tag` or list).
  `rolls`/`count` are `ContextIntProviders` (bare int, or `{"type":"minecraft:uniform","min","max"}` with int bounds); a typeless `{"min","max"}` no longer parses.
  `bonus_rolls: 0.0` and `add: false` are defaults (dropped). `copy_name {source: "block_entity"}`, `apply_bonus`, `table_bonus`, `explosion_decay`, `survives_explosion`,
  `alternatives`, `inverted {term}`, `any_of/all_of {terms}` keep their fields.
- (resources) **Advancements**: `recipe_unlocked` field `recipe` -> `recipes` (`Recipe.LIST_CODEC`; a single id string is fine). Every `LootItemCondition` field on a
  trigger (`player`, `location` on `placed_block`/`item_used_on_block`/`location`) is now ONE condition (`"type"`-keyed object or reference string), not a list ->
  wrap in `all_of`. `inventory_changed` (`items`), `location_check`, `entity_properties`, `match_tool` shapes unchanged.
- (resources) **Worldgen data**: `data/<ns>/worldgen/configured_feature/` -> `worldgen/feature/`, JSON flat (no `config`). `BlockState` JSON is `{"id","properties"}`
  (`BlockState.CODEC` also accepts a bare block id for the default state). `BlockStateProvider` type names lost their suffix: `simple_state_provider` -> `simple`
  (or just write the bare state), `weighted_state_provider` -> `weighted`, `rule_based_state_provider` -> `rule_based` (`BlockStateProviderTypes`); providers are
  `Holder`s (`worldgen/block_state_provider/` registry, e.g. `"minecraft:soil_beneath_tree"`) but inline objects still work. `minecraft:tree` fields:
  `trunk_provider, trunk_placer, foliage_provider, foliage_placer, root_placer?, minimum_size, decorators, ignore_vines, below_trunk_provider` (`TreeFeature`);
  `sapling_provider`/`dirt_provider` no longer exist (unknown keys are ignored). `simple_block {to_place}` unchanged. Placement: `random_offset {xz_spread,y_spread}` ->
  `offset {x,y,z}` (`OffsetPlacement`, each an IntProvider in [-16,16]); `rarity_filter, in_square, heightmap, biome, count, block_predicate_filter,
  surface_water_depth_filter` and block predicates `matching_blocks, matching_block_tag, not, all_of, would_survive` are unchanged.
- (resources) **Unchanged 26.2 -> 26.3 schemas** (file-level diff of vanilla + codec check): recipes (`crafting_shaped/shapeless`, `smithing_transform`; only `group`
  names and smelting `cookingtime` defaults changed), tags, `villager_trade`/`trade_set` (vanilla just writes `max_uses`/`xp`/`count`/`amount` as ints now; codec
  fields `wants, additional_wants?, gives, max_uses, xp, reputation_discount, merchant_predicate?, given_item_modifier?, double_trade_price_enchantments?`),
  `items/*.json` (`minecraft:model`, `minecraft:grass`), `equipment/*.json` (new optional `trim_overrides` only), blockstates, sounds, lang.
- (resources) **Block models**: the element boolean `"shade"` is gone from `CuboidModelElement`; vanilla replaced `"shade": false` with `"shade_direction_override": "up"`
  (`template_fire_side.json`) and simply dropped `"shade": true`.
- (resources) `fabric.mod.json` `"minecraft": ">=26.3-"` (trailing `-` = empty prerelease, so `26.3-rc-2` satisfies it); `neoforge.mods.toml` ranges `[26.3,)`.

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

## Runtime / toolchain findings (coordinator, 2026-09-13, after the four ports were merged)
- `./gradlew build` passes; `fabric/build/libs/letsdo-vinery-fabric-1.6.0.jar` is produced.
- **Architectury runtime transformer crash** on every dev run: `Unsupported class file major version 71`. Cause: LWJGL 3.4.3
  (new in 26.3) ships `META-INF/versions/27/**.class` (Java 27), and the transformer (5.2.91, latest) bundles an ASM too old to read
  them while analysing the classpath. Fix in `fabric/build.gradle`: a `architecturyTransformerClasspath` configuration (the plugin
  prefers it over `compileClasspath` when present) with `exclude group: "org.lwjgl"`, plus LWJGL filtered from `runServer`'s classpath.
  `runClient` needs LWJGL and is therefore still expected to hit this until architectury-transformer updates ASM.
- **REI 26.2.821** declares `minecraft >=26.2 <26.3-` and refuses to load on 26.3-rc-2. `fabric/build.gradle` uses `compileOnly` for
  `RoughlyEnoughItems-fabric` until a 26.3 REI exists (REI plugin classes still compile; entrypoints are only touched when REI is present).
- **Architectury API 21.1.9** (26.2 build) fails at mixin apply on 26.3: `MixinServerPlayer.dropItem` targets `drop(ItemStack,ZZ)`,
  which is now `drop(ItemStack, boolean, Prediction)`. Architectury has an unreleased `26.3` branch on GitHub (version `22.0`, Fabric only,
  "bump to rc-2" 2026-09-11). It was cloned to `build/architectury-api` and published to `~/.m2` as `dev.architectury:architectury{,-fabric}:22.0.9999`
  (`GITHUB_RUN_NUMBER` unset -> `9999`); `mavenLocal()` was added to the root repositories and `architectury_version=22.0.9999`.
  Replace with the real release when it appears on maven.architectury.dev.
- **Recipes are a datapack registry now**: a recipe-unlock advancement whose `rewards.recipes` / `recipe_unlocked.recipes` names a recipe
  file that does not exist fails registry loading (`Unbound values in registry minecraft:recipe`). Six advancements were repointed to the
  real recipe ids (`wine_press`, `dark_cherry_shelf`, `dark_cherry_big_table`, `winemaker_*`) and four stale ones deleted
  (`basket`, `flower_box`, `flower_pot`, `grapevine_lattice` — no such recipes). `build/advcheck.py` cross-checks this.
