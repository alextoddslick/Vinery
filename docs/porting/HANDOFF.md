# Vinery version-port handoff (updated 2026-09-11, late)

## Start here (for an AI or a human picking this up)
1. All three port branches are DONE and build: `1.21.10`, `26.1`, `26.2` (each based on the previous; nothing pushed).
   What has NOT happened: any client test. See "Next steps".
2. Read this file's "Status", "How to build", "Process that worked", then `PORTING_NOTES_26.1.md` and `PORTING_NOTES_26.2.md`
   ("Verified during the port" sections = the API facts learned the hard way).
3. Reference sources (decompiled MC, vanilla data, dependency sources) live in a temp scratchpad that may be gone —
   "Regenerating the reference sources" explains how to rebuild them.
4. Alex only cares about Fabric; NeoForge is best-effort. Never run the client/simulator yourself; he tests.

Goal: port Vinery incrementally `1.21.1 -> 1.21.10 -> 26.1 -> 26.2`, one git branch per version, each based on the
previous. **Fabric is the priority; NeoForge is best-effort** (Alex only cares about Fabric).

## Status

| Branch    | State |
|-----------|-------|
| `1.21.1`  | original upstream code (unchanged) |
| `1.21.10` | **DONE.** `./gradlew build` produces `fabric/build/libs/letsdo-vinery-fabric-1.6.0.jar` and the NeoForge jar. Both dedicated servers boot to "Done" in the dev runtime (`:fabric:runServer`, `:neoforge:runServer`). Not tested in a client. |
| `26.1`    | **DONE (Fabric verified, NeoForge best-effort).** `./gradlew build` produces `fabric/build/libs/letsdo-vinery-fabric-1.6.0.jar` and the NeoForge jar. Fabric dedicated server boots to `Done` with zero errors; NeoForge dedicated server boots to `Done` too (its log shows REI's own `LocalPlayer` dist-cleaner failure and Architectury-generated `@OnlyIn` warnings, neither caused by Vinery). Not tested in a client. See "26.1 result" below. |
| `26.2`    | **DONE (Fabric verified, NeoForge best-effort).** `./gradlew build` produces both jars; Fabric and NeoForge dedicated servers boot to `Done` with zero Vinery errors. Not tested in a client. See "26.2 result" below. |

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

Dedicated-server smoke test (catches registry/mixin/class-loading/codec errors that compilation cannot):
`mkdir -p fabric/run && echo eula=true > fabric/run/eula.txt && ./gradlew :fabric:runServer --console=plain > server.log 2>&1 &`,
then poll `server.log` for `Done (` (macOS has no `timeout`; kill with `pkill -f DevLaunchInjector`). Same with `neoforge/run` and
`:neoforge:runServer`. Grep the log for `ERROR|Caused by`; REI's `LocalPlayer` failure and `@OnlyIn` warnings on NeoForge are not ours.
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

## 26.1 result (2026-09-11)
Five parallel Opus agents (core / villagers+mixins / client / recipes+compat / resources) each fixed a disjoint file set on
the `26.1` base; the coordinator merged, then fixed two runtime-only problems the smoke test found:
Architectury 20.0.4–20.0.6 (undeclared access widener -> bumped to 20.1.14, metadata requires >=20.0.7) and recipe results
having to be `ItemStackTemplate`. All facts are in `PORTING_NOTES_26.1.md` ("Verified during the port").

Things to know before a client test:
- Client-only code (screens, block-entity renderers, banner renderer, armor renderers, colour handlers) compiles but was never
  rendered. Screens were rewritten to the extract model; the banner renderer follows vanilla `BannerRenderer`.
- Villager trades are now data (`data/vinery/trade_set`, `villager_trade`, `tags/villager_trade`); `max_uses` follows the old
  config's declared values, which were lower than what the buggy old runtime produced (8/12). Level-4 seed trades now use the
  real item ids (`taiga_grape_seeds_red/_white`), which the old config had wrong.
- The Fabric and NeoForge config classes lost their trade lists.
- Pre-existing (not port) data bugs found by the resources agent, left untouched: advancement `items[].tag`/`.nbt` predicates are
  ignored since 1.20.5 (`recipes/{campfire,grapevine_lattice,dark_cherry_planks}`, `main/vintage_perfection`); `main/root.json`
  background path resolves to `textures/textures/...png.png`; `c:` tags use Fabric v1 names (`c:berries` -> `c:foods/berry`,
  `c:stripped_wood` -> `c:stripped_woods`); 4 orphaned `structure/*.nbt`. Fabric applies the XP-orb bonus twice (two mixins).
- NeoForge: the winemaker POI goes through `DeferredRegister`, which never fills `PoiTypes.TYPE_BY_STATE`, so the job site may
  not work there; POI search range differs (Fabric 12, NeoForge 1).

## 26.2 result (2026-09-11)
Two Opus agents (all Java / all resources) on the `26.2` base, merged by the coordinator; only 24 compile errors existed.
API facts are in `PORTING_NOTES_26.2.md`. Notable:
- Signs are ordinary block models now (sign atlas and `Sheets` sign members deleted; sign renderers draw text only). Dark cherry
  signs got vanilla-shaped blockstates (16/32/4/4 variants), 14 `template_*`-parented models, and 32x32 block textures derived
  from the old 64x32 entity sheets by a face-rect mapping that reproduces all 12 vanilla woods pixel-for-pixel.
- `VineryWoodType.DARK_CHERRY` is now named `dark_cherry` (was `vinery:dark_cherry`): `SignEditScreen`/`HangingSignEditScreen`
  build `minecraft:textures/gui/{signs,hanging_signs}/<name>.png` and a `:` in the path threw. The GUI textures now live under
  `assets/minecraft/textures/gui/...`. This was a pre-existing client crash for hanging signs since 26.1.
- `EntityType` constants moved to `EntityTypes`; `InstantenousMobEffect` -> `InstantaneousMobEffect`; `net.minecraft.util.Tuple`
  deleted (use `Vec2`); `LightEngine.getLightDampeningInto`; `Sheets.addWoodType` gone even on NeoForge.
- Data: no schema changes for loot tables, recipes, tags, worldgen, trade sets, item definitions, equipment; one advancement needed
  `location` -> `minecraft:location` (entity-predicate dispatch). `TreeConfiguration.below_trunk_provider` is required (already set).
- Pre-existing issues still untouched: 64 dangling model/texture references in files nobody edited (`drawer*`, `red_vine*`,
  `*grapejuice`, `apple_juice`, `wine_bottle`, ...), the `c:` v1 tag names, `sapling_provider` in tree features, and the
  advancement predicate bugs listed under "26.1 result".

## Next steps (not done)
1. Client test on Fabric 26.2 (and 26.1): open every GUI (apple press, fermentation barrel), place/edit standing and hanging
   dark cherry signs, look at every block-entity renderer (storage blocks, lattice, completionist banner), boats, armor, villager
   trades, REI/JEI pages.
2. Decide whether to fix the pre-existing data bugs above.
