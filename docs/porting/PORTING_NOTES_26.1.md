# Vinery port: Minecraft 1.21.10 -> 26.1.2 (Architectury 20, Fabric + NeoForge)

Several agents port in parallel on DISJOINT file sets. Do NOT edit files outside your assignment; describe
needed changes elsewhere in your final report. **Fabric is the priority; NeoForge is best-effort** (keep it
compiling if cheap, otherwise report what is broken).

## FIRST: make sure your worktree is on the right base
The worktree tool sometimes creates the worktree from an old commit. In your worktree run:
```
git fetch -q . 2>/dev/null; git merge 26.1 2>/dev/null || git reset --hard 26.1
grep minecraft_version gradle.properties    # MUST print minecraft_version=26.1.2
```
If it prints anything else, run `git reset --hard 26.1` (the `26.1` branch of the main repo is the base).

## Environment / build
- Java 25 is REQUIRED (Minecraft 26.1 ships unobfuscated with official names; Loom "no-remap" 1.17, Gradle 9.5.1):
```
export JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home CURSEFORGE_API_KEY=x
./gradlew :common:compileJava --continue -q 2>&1 | grep -E "error:|symbol:|location:" | grep -E "<paths you own>"
```
  First run in a fresh worktree takes a few minutes. Other agents' files are also broken; filter to your own paths.
  `:fabric:compileJava` / `:neoforge:compileJava` only work once `common` compiles.
- There are NO `modImplementation`/`modApi` configurations any more (plain `implementation`/`api`); nothing is remapped.
- javac has `-Xmaxerrs 5000`. Never run the game, never push.

## Reference sources (grep these; do not guess APIs)
Base dir `S=/private/tmp/claude-501/-Users-alextodd-temp-Github-NOTSYNCED-Vinery/0be9815f-f3f4-4135-a2e2-0a80aae3a5d1/scratchpad`
- Minecraft 26.1.2 decompiled (real parameter names): `$S/mc26.1/net/minecraft`   (previous version for diffing: `$S/mc1.21.10/net/minecraft`)
- Vanilla 26.1.2 data + assets json: `$S/mcres26.1/{data,assets}/minecraft`
- Class move/rename table 1.21.10 -> 26.1: `$S/renames-1.21.10-to-26.1.txt` (already applied to imports; use it when a class "disappears")
- Fabric API 0.155.3+26.1.2 (all modules): `$S/ref26/fapi`
- Architectury API 20.0.4: common `$S/ref26/arch`, fabric `$S/ref26/archfabric`, neoforge `$S/ref26/archneo`
- NeoForge 26.1.2.109: `$S/ref26/neo`
- REI 26.1.819: api `$S/ref26/rei-api`, default plugin `$S/ref26/rei-default`, fabric `$S/ref26/rei-fabric`, neoforge `$S/ref26/rei-neoforge`
- JEI 29.37.0.98: api `$S/ref26/jei-common`, neoforge api `$S/ref26/jei-neoforge-api`, full neoforge `$S/ref26/jei-neoforge`
- ModMenu 18.0.1 `$S/ref26/modmenu`, cloth-config 26.1.154 `$S/ref26/cloth`
- The exact classpath jar (for `javap -p -cp <jar> <class>` when you need real access modifiers):
  `/Users/alextodd/temp/Github-NOTSYNCED/Vinery/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-f1edf155fd/26.1.2/minecraft-merged-f1edf155fd-26.1.2.jar`
  (`javap` = `/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home/bin/javap`)
`grep` here is ugrep: use `grep -E`, and quote `$` (`grep -F 'Foo$Bar'`).

## Already done on the 26.1 base (do not redo)
- Gradle/Loom/Java toolchain; `ResourceLocation` -> `Identifier` (`net.minecraft.resources.Identifier`, same static factories:
  `fromNamespaceAndPath`, `parse`, `withDefaultNamespace`) everywhere; package moves from the rename table applied to imports
  (e.g. `RenderType` -> `client.renderer.rendertype`, `Material` -> `client.resources.model.sprite`, `Boat`/`ChestBoat` ->
  `world.entity.vehicle.boat`, `Llama`/`AbstractChestedHorse` -> `world.entity.animal.equine`, `Fox` -> `world.entity.animal.fox`,
  `WanderingTrader` -> `world.entity.npc.wanderingtrader`, `Villager` -> `world.entity.npc.villager`, `GameRules` ->
  `world.level.gamerules`, `BoatModel` -> `client.model.object.boat`, `Util` -> `net.minecraft.util.Util`, `VillagerTrades` ->
  `world.item.trading`).
- Access widener (`common/src/main/resources/vinery.accesswidener`, now `v2 official`) exposes: `MobEffectInstance.duration/amplifier`,
  `PoiTypes.TYPE_BY_STATE`, `WoodType.register`, `BlockEntityType.<init>` + `BlockEntityType$BlockEntitySupplier`,
  protected ctors of `SaplingBlock(TreeGrower,Properties)`, `StairBlock(BlockState,Properties)`, `TrapDoorBlock/PressurePlateBlock/DoorBlock(BlockSetType,Properties)`,
  `ButtonBlock(BlockSetType,int,Properties)`, `FireBlock.setFlammable`, `MenuType.<init>(MenuSupplier,FeatureFlagSet)` + `MenuType$MenuSupplier`,
  `CreativeModeTab$Output`, `Level.random`, `BlockEntityRenderState.blockState`. If you need more entries, put them in your report
  (the coordinator owns the AW file) — use `javap` for exact descriptors.

## Known 26.1 API facts (verified)
- `ChunkPos` is a record: `ChunkPos.containing(BlockPos)`, `pos.x()`/`pos.z()`.
- `Level.getRandom()` exists; `Level.random` is protected (widened, but prefer `getRandom()`).
- `Item.getName()` (no-arg) is gone; use `getName(ItemStack)` or `Component.translatable(getDescriptionId())`.
- `Recipe<T>`: `assemble(T input)` (no registry provider), `String group()` and `boolean showNotification()` are abstract,
  `Recipe.BookInfo`/`Recipe.CommonInfo` records with `MAP_CODEC`s exist for codecs (see `ShapedRecipe`).
- Villager trades are DATA-DRIVEN: registry `trade_set` (`data/<ns>/trade_set/<name>.json`, see
  `$S/mcres26.1/data/minecraft/trade_set/wandering_trader/common.json`, `.../farmer/level_1.json`) referencing
  `villager_trade` entries (`data/minecraft/villager_trade/**`) and tags. `net.minecraft.world.item.trading.VillagerTrades`
  no longer has `ItemsForEmeralds`/`EmeraldForItems`; `AbstractVillager.addOffersFromTradeSet(ServerLevel, MerchantOffers, ResourceKey<TradeSet>)`
  is how mobs get offers (see `WanderingTrader.updateTrades`, `Villager.updateTrades`, `TradeSets`, `VillagerTrade`, `TradeCost`).
  Fabric POI: `net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper.register(Identifier, int, int, Block...)`.
  `VillagerProfession` is still a code registry (record in `world.entity.npc.villager`).
- Block render layers: `ItemBlockRenderTypes`, `BlockRenderLayerMap` and Architectury `RenderTypeRegistry` are GONE. The layer is now
  derived from the model/texture: cutout is automatic; translucent textures declare `{"force_translucent": true, "sprite": "..."}`
  in the model's `textures` (see `$S/mcres26.1/assets/minecraft/models/block/glass.json`). Delete the Java registrations and tell
  the resources agent which blocks need `force_translucent` (window / window_block used TRANSLUCENT).
- Architectury 20 client registries: `dev.architectury.registry.client.rendering.{BlockEntityRendererRegistry, ColorHandlerRegistry}`
  (`registerBlockColors(BlockTintSource, Block...)`), `registry/client/level/entity/{EntityRendererRegistry, EntityModelLayerRegistry}`,
  `registry/client/gui/MenuScreenRegistry`. Check `$S/ref26/arch` for the rest (menus, creative tabs, fuel, networking).
- `EntityType.EntityFactory<T>.create(EntityType<T>, Level)` unchanged; `MobRenderer`/`EntityRenderer` generics tightened
  (`T extends Mob`/`LivingEntity` bounds) — see errors like "TraderMuleEntity cannot be converted to PathfinderMob" (class hierarchy changed:
  `Llama`/`AbstractHorse` moved to `world.entity.animal.equine`; re-check the superclass chain).
- `BlockEntityRenderState.blockState` is private (widened) — vanilla uses `state.blockState` internally; check for a getter.
- `GuiGraphics` was replaced by `net.minecraft.client.gui.GuiGraphicsExtractor` (same role: `blit(RenderPipeline, Identifier, x, y, u, v, w, h, texW, texH)`,
  `drawString`, ...); `AbstractContainerScreen.renderBg(GuiGraphicsExtractor, float, int, int)` / `renderLabels(GuiGraphicsExtractor, int, int)`.
  `imageWidth/imageHeight` are final now — check how vanilla screens (e.g. `FurnaceScreen`/`AbstractFurnaceScreen`) set them.
- JEI 29: `IRecipeCategory.getWidth()/getHeight()` are abstract.
- Pack formats 26.1.2: resource 84, data 101.1 (`version.json`: resource_major 84, data_major 101, data_minor 1).
- Recipe json / loot / advancement formats: compare with `$S/mcres26.1/data/minecraft/**`.

## Report format
Files changed/added/deleted; remaining errors in your files; exact changes needed in files you do not own (AW lines with descriptors,
resource files, registry names). Commit on your branch before reporting.

## Verified during the port (2026-09-11) — facts the agents established
- **Architectury 20.0.4–20.0.6 Fabric jars ship `architectury.accessWidener` but do not declare it in `fabric.mod.json`**, so
  `AxeItemHooks.addStrippable` throws `IllegalAccessError` at startup. 20.0.7+ is fixed; the branch uses 20.1.14 and the
  mod metadata requires `>=20.0.7`.
- `ItemStack.CODEC` now validates `Item.areComponentsBound()` and fails while recipes are parsed ("Item X does not have
  components yet"). Recipe results must be `ItemStackTemplate` (`ItemStackTemplate.CODEC`/`STREAM_CODEC`, `.create()` at use
  time), exactly like vanilla `SmithingTransformRecipe`. JSON shape `{id,count,components}` is unchanged.
- `RecipeSerializer<T>` is a record `(MapCodec, StreamCodec)`; `assemble(T)` has no registry argument; `group()` and
  `showNotification()` are abstract. JEI 29: `getBackground()` removed, `getWidth()/getHeight()` abstract, `GuiGraphicsExtractor`;
  categories work on `RecipeHolder<T>` via `IRecipeHolderType.create(Identifier)`. REI 26.1: `RecipeHolder.id().identifier()`.
- Screens: no `render`/`renderBg`/`renderLabels`. Use `extractBackground(GuiGraphicsExtractor,int,int,float)`,
  `AbstractContainerScreen.extractRenderState(...)`, `extractLabels(...)`; `drawString` -> `graphics.text(...)`; label colours are
  ARGB (`-12566464`, not `4210752`); `imageWidth/imageHeight` are constructor arguments. Template: `AbstractFurnaceScreen`.
- `SubmitNodeCollector.submitBlock` is gone: use `BlockModelRenderState` + `context.blockModelResolver()` (`BlockModelResolver.update`
  during extraction, `renderState.submit(poseStack, collector, light, overlay, outline)` on render). Templates: `CarriedBlockLayer`.
- `Entity.hasImpulse` -> `Entity.needsSync`. `ModelBakery.BANNER_BASE`/`MaterialSet` -> `Sheets.BANNER_BASE` (`SpriteId`) +
  `context.sprites().get(...)`. `RenderTypes.entityCutout` == old `entityCutoutNoCull`; old `entityCutout` == `entityCutoutCull`.
  `LightTexture.pack` -> `LightCoordsUtil.pack`. `ColorHandlerRegistry.registerBlockColors` takes `BlockTintSources.grass()/foliage()`.
  Block-entity render states are created every frame (not pooled). `Sheets.addWoodType` exists only in NeoForge's patched jar.
- `BlockBehaviour.getLightBlock` -> `getLightDampening`. `ItemContainerContents.nonEmptyItems()` yields `ItemStackTemplate`s.
  `Commands.hasPermission(LEVEL_GAMEMASTERS)` needs an explicit type witness when chained with `.and(...)`.
- Fabric API 0.155: `ResourceManagerHelper`/`ResourcePackActivationType` removed -> `ResourceLoader.registerBuiltinPack(Identifier,
  ModContainer, PackActivationType)`; `TradeOfferHelper` removed. Villager trades live in `data/vinery/{trade_set,villager_trade,
  tags/villager_trade}` (`vinery:winemaker/level_1..5`, `vinery:wandering_winemaker/common`); `VillagerProfession` takes
  `Int2ObjectMap<ResourceKey<TradeSet>>`; `AbstractVillager.addOffersFromTradeSet` is protected (no AW needed).
- Mixins: `SpreadingSnowyDirtBlock` -> `SpreadingSnowyBlock`, and `BlockState.is` resolves to `TypedInstance#is(Ljava/lang/Object;)Z`
  in `@At` targets; `WanderingTraderManager` lost `serverLevelData`/`setWanderingTraderId`. Mixin configs need
  `"compatibilityLevel": "JAVA_25"` (class-file v69).
- Data: `minecraft:random_patch`/`minecraft:flower` features are gone (use `simple_block` + `count`/`random_offset` placement);
  `TreeConfiguration` `dirt_provider`+`force_dirt` -> `below_trunk_provider`; equipment json needs a `humanoid_baby` layer (no
  fallback); pack format 84 needs `min_format`/`max_format`. Recipes, advancements, loot tables, tags, blockstates, item
  definitions had NO schema change between 1.21.10 and 26.1.2.
- Dedicated-server smoke test: `fabric/run/eula.txt` + `:fabric:runServer`, kill after `Done (` (macOS has no `timeout`; a poll
  loop with `pkill -f DevLaunchInjector` works).
