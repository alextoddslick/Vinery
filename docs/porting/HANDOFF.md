# Vinery version-port handoff (written 2026-09-11)

Goal: port Vinery incrementally `1.21.1 -> 1.21.10 -> 26.1 -> 26.2`, one git branch per version, each based on the
previous. **Fabric is the priority; NeoForge is best-effort** (Alex only cares about Fabric).

## Status

| Branch    | State |
|-----------|-------|
| `1.21.1`  | original upstream code (unchanged) |
| `1.21.10` | **DONE.** `./gradlew build` produces `fabric/build/libs/letsdo-vinery-fabric-1.6.0.jar` and the NeoForge jar. Both dedicated servers boot to "Done" in the dev runtime (`:fabric:runServer`, `:neoforge:runServer`). Not tested in a client. |
| `26.1`    | **IN PROGRESS.** Toolchain converted and committed (`chore: 26.1 toolchain ...`); ~170 real compile errors remained in `common` (see `compile-errors-26.1-after-renames.log`). Five parallel agents were working on disjoint slices and were told to commit WIP; their branches are named `worktree-agent-*` (merge them into `26.1` if not already merged; they may not compile). |
| `26.2`    | not started. Reference sources were downloaded (see below); class rename table `renames-26.1-to-26.2.txt` is in this folder. |

## How to build

```
# 1.21.10 branch
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home CURSEFORGE_API_KEY=x
./gradlew build

# 26.1 / 26.2 branches (Minecraft ships unobfuscated; Loom "no-remap"; Gradle 9.5.1; Java 25)
export JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home CURSEFORGE_API_KEY=x
./gradlew :common:compileJava --continue -q 2>&1 | grep -E "error:|symbol:|location:"
```
`CURSEFORGE_API_KEY` must be set to anything, otherwise the cursegradle plugin fails configuration.
javac runs with `-Xmaxerrs 5000`. The `/Library/Java/.../openjdk-25.jdk` install is x86_64; use the Homebrew arm64 one above.

Dedicated-server smoke test (catches registry/mixin/class-loading errors that compilation cannot):
`mkdir -p fabric/run && echo eula=true > fabric/run/eula.txt && ./gradlew :fabric:runServer` and look for `Done (`.
On NeoForge the dev dist-cleaner throws if common code touches client classes (that is how the `ClientLevel` bug in
`DrinkBlockItem` was found on 1.21.10 — client access now goes through `client/util/ClientUtil`).

## What changed in the 1.21.10 port (for orientation)
- Every Block/Item gets its registry id via `GeneralUtil.blockProps/itemProps/blockKey/itemKey` (mandatory since 1.21.2).
- Boats use vanilla `Boat`/`ChestBoat` entity types (`EntityTypeRegistry.DARK_CHERRY_BOAT/_CHEST_BOAT`) + vanilla `BoatRenderer`.
- Armor: plain `Item`s with `humanoidArmor(ArmorMaterialRegistry.WINEMAKER_ARMOR, type)`, equipment asset `vinery:winemaker`;
  custom armor models are `HumanoidModel<HumanoidRenderState>` registered via Fabric `ArmorRenderer` / NeoForge
  `IClientItemExtensions#getHumanoidArmorModel`.
- Block entity renderers use the 1.21.9+ extract/submit model (`*RenderState` classes under `client/render`).
- Signs use vanilla `SignRenderer`/`HangingSignRenderer` (WoodType `vinery:dark_cherry`).
- Recipes: clients do not get recipes any more. REI uses a `REICommonPlugin` (server-side display fillers + serializers);
  JEI is fed from `core/network` (`VineryRecipeSyncPayload` sent on join / reload into `VineryClientRecipeCache`).
- Block tooltips go through `BlockTooltip` + `VineryBlockItem`.
- Resources: `assets/vinery/items/*.json` item definitions generated for all 158 items; recipe ingredients are strings;
  equipment json + textures; loot tables/tags fixed. `pack.mcmeta` in the NeoForge module deliberately declares no format.
- Villager POI/profession still code-registered; trades from the config lists (Fabric `TradeOfferHelper`, NeoForge event).

## 26.1 work: what is known
Read `PORTING_NOTES_26.1.md` (API facts verified against the decompiled 26.1.2 sources) and the rename table.
Big-ticket items for 26.1:
1. Villager trades became data-driven (`data/<ns>/trade_set/**`, `villager_trade/**`, `AbstractVillager.addOffersFromTradeSet`);
   the config-driven trade lists cannot survive as-is.
2. Block render layers are texture-driven: delete `RenderTypeRegistry` calls; models of translucent blocks
   (`window`, `window_block`) need `{"force_translucent": true, "sprite": ...}` textures.
3. `GuiGraphics` -> `GuiGraphicsExtractor`; `AbstractContainerScreen` takes `imageWidth/imageHeight` in the constructor.
4. `Recipe.assemble(T)` lost the registry argument; `group()`/`showNotification()` abstract; codecs via `Recipe.CommonInfo`.
5. JEI 29 (`IRecipeCategory.getWidth/getHeight` abstract), REI 26.1 (`EntryIngredients` now takes `ItemStackTemplate`s),
   Architectury 20 (`ColorHandlerRegistry.registerBlockColors(BlockTintSource, ...)`).
6. Pack formats: resource 84, data 101.1. 26.2: resource 88, data 107.1.
7. Access widener is `v2 official` now; extra entries were added for protected vanilla constructors (see the file).

## 26.2
Toolchain versions (from architectury-api `26.2` branch): Architectury 21.1.9, Fabric API 0.160.0+26.2, NeoForge 26.2.0.87,
REI 26.2.820, JEI 30.32.0.215 (artifact `jei-26.2-*`), cloth-config 26.2.155, modmenu 20.0.2, Fabric loader 0.19.5,
same Gradle/Loom/Java as 26.1. Class moves 26.1 -> 26.2 are small (85 moves, see the table).

## Regenerating the reference sources (they lived in a temp scratchpad and may be gone)
- Decompile a vanilla client jar (26.x is unobfuscated): download from piston-meta, then
  `java -jar vineflower-1.11.1.jar client.jar out/` (Vineflower from Maven Central `org.vineflower:vineflower:1.11.1`).
- 1.21.10 sources: `./gradlew :common:genSources` on the `1.21.10` branch writes a `-sources.jar` under `.gradle/loom-cache`.
- Dependency sources: `<artifact>-<version>-sources.jar` from maven.fabricmc.net (per Fabric API module; the umbrella pom lists
  module versions), maven.architectury.dev, maven.neoforged.net/releases, maven.shedaniel.me, maven.blamejared.com,
  maven.terraformersmc.com/releases. Vanilla data/assets: unzip `data/* assets/* version.json` from the client jar.

## Process that worked
Coordinator prepares the branch (toolchain, mechanical renames, access widener, notes file), then spawns parallel agents in git
worktrees on disjoint file sets (registries/items/util, blocks + block entities, entities/mixins/loader glue, client, recipes/compat,
resources). Agents filter compile output to their own paths, commit on their worktree branch; coordinator merges and fixes
integration errors, then runs `build` and the dedicated-server smoke tests. Check each worktree's `gradle.properties` right after
spawning: the worktree tool sometimes bases worktrees on the wrong commit.

## 26.1: verified findings from the (stopped) agents — apply these next

**Core (registries/items/blocks/util)** — only ~20 real errors remain in this area:
- `GeneralUtil`, `JungleGrapeFeature`: `new ChunkPos(pos)` -> `ChunkPos.containing(pos)`, `.x/.z` -> `.x()/.z()`;
  `level.dimension().location()` -> `.identifier()`; `getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)` ->
  `getGameRules().get(GameRules.BLOCK_DROPS)` (`net.minecraft.world.level.gamerules.GameRules`).
- `WineDebugCommands`: `source.hasPermission(2)` -> `Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)` (a `PermissionProviderCheck`, `and` it with the ServerPlayer check).
- `BasketItem`: `ItemContainerContents.nonEmptyStream()` -> `nonEmptyItemCopyStream()`; `new BundleContents(...)` takes `List<ItemStackTemplate>` (`ItemStackTemplate.fromNonEmptyStack`).
- `FoodComponent`: `ItemStack.SINGLE_ITEM_CODEC` is gone (use `ItemStackTemplate.CODEC` or drop the field).
- `AppleLeavesBlock`/`DarkCherryLeavesBlock` ~line 170: an `@Override` no longer matches (check leaves `randomTick` in 26.1); `SpreadableGrassSlabBlock:86`: `BlockState.getLightBlock()` is gone.
- `VillagerUtil`: replace the `ItemListing` factories with `ResourceKey<TradeSet>` constants.

**Entities / trades / mixins**: `TraderMuleEntity` and `ChairEntity` already compile. `VillagerProfession` is a 7-arg record whose last
argument is `Int2ObjectMap<ResourceKey<TradeSet>> tradeSetsByLevel` (`Villager.updateTrades` uses `profession.getTrades(level)`), so
`VineryFabricVillagers`/`VineryNeoForgeVillagers` must pass `vinery:winemaker/level_1..5`. Json: `trade_set/<name>.json` =
`{amount, random_sequence, trades: "#ns:tag"}`, tag under `tags/villager_trade/**`, each `villager_trade/<path>.json` =
`{wants:{id,count}, gives:{id,count}, max_uses, xp, reputation_discount}`. `WanderingWinemakerEntity.updateTrades` -> `addOffersFromTradeSet`
(protected on `AbstractVillager`). Re-target `SpreadingSnowyDirtBlockMixin` (-> `SpreadingSnowyBlock`) and `WanderingTraderManagerMixin:75`.
Fabric POI: `PoiHelper`. Strip the config trade lists + `TradeOfferHelper`/`VillagerTradesEvent` code.

**Recipes / compat**: `RecipeSerializer<T>` is a RECORD `(MapCodec, StreamCodec)` — replace the inner `Serializer` classes with
`new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC)` (see `StonecutterRecipe`); `assemble(T)` (block entities must call
`recipe.assemble(input)`); `group()`/`showNotification()` abstract; `RecipeHolder.id()` is a `ResourceKey` (`.identifier()`);
JEI: `GuiGraphicsExtractor`, `getWidth/getHeight` abstract; REI `EntryIngredients` now takes `ItemStackTemplate`s.
`core/network/**` already compiles on 26.1.

**Client**: `LightTexture.pack` -> `net.minecraft.util.LightCoordsUtil.pack` (or `LevelRenderer.getLightCoords`); `RenderType.xxx(...)`
factories -> `net.minecraft.client.renderer.rendertype.RenderTypes` (`entityCutoutNoCull` is gone: use `entityCutout`);
`SignRenderer` -> `StandingSignRenderer`; `MaterialSet` and `ModelBakery.BANNER_BASE` gone (template: vanilla `BannerRenderer`);
`SubmitNodeCollector.submitBlock` signature changed; screens -> `GuiGraphicsExtractor` + constructor `imageWidth/imageHeight`;
`ColorHandlerRegistry.registerBlockColors(BlockTintSource, ...)`; delete all `RenderTypeRegistry` calls.

**Resources**: bump `bushy_leaves/pack.mcmeta` to 84; `force_translucent` on `window`/`window_block` models; vanilla diffs show
heavy changes in recipes (709/1448 files), placed/configured features, biomes, advancements (all), some tags — diff vanilla
1.21.10 vs 26.1.2 pairs to derive the schema deltas before scripting the rewrite.
