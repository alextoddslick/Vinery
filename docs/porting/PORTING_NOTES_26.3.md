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
