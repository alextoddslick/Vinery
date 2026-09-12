# Vinery port: Minecraft 26.1.2 -> 26.2 (Architectury 21.1, Fabric + NeoForge)

Same process and rules as `PORTING_NOTES_26.1.md` (read it first, including the "Verified during the port" section — everything
there still applies). Several agents port in parallel on DISJOINT file sets; edit only your assignment; **Fabric is the priority,
NeoForge is best-effort**.

## Base check (worktrees are sometimes created from the wrong commit)
```
grep minecraft_version gradle.properties    # MUST print minecraft_version=26.2
```
If not: `git reset --hard 26.2` (branch of the main repo at /Users/alextodd/temp/Github-NOTSYNCED/Vinery).

## Build
```
export JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home CURSEFORGE_API_KEY=x
./gradlew :common:compileJava --continue -q 2>&1 | grep -E "error:|symbol:|location:" | grep -A2 -E "<your paths>"
```
Platform modules compile only once all of `common` does (`:fabric:compileJava`, `:neoforge:compileJava`).

## Toolchain (already applied on the `26.2` base)
Minecraft 26.2 (pack formats: resource 88, data 107.1), Architectury 21.1.9, Fabric API 0.160.0+26.2, Fabric loader 0.19.5,
NeoForge 26.2.0.87, REI 26.2.821, JEI 30.32.0.215 (artifact `jei-26.2-*`), cloth-config 26.2.155, ModMenu 20.0.2.
Same Gradle 9.5.1 / loom-no-remap 1.17 / Java 25 / AW `v2 official` as 26.1. The 26.1->26.2 class-move table
(`renames-26.1-to-26.2.txt`) has been applied to imports (only `CriteriaTriggers` moved for us).

## Reference sources
Base dir `S=/private/tmp/claude-501/-Users-alextodd-temp-Github-NOTSYNCED-Vinery/0be9815f-f3f4-4135-a2e2-0a80aae3a5d1/scratchpad`
- Minecraft 26.2 decompiled: `$S/mc26.2/net/minecraft`; previous version for diffing: `$S/mc26.1/net/minecraft`
- Vanilla 26.2 data + assets: `$S/mcres26.2/{data,assets}/minecraft`; 26.1.2: `$S/mcres26.1/...`
- Dependency sources for 26.2: `$S/ref262/{fapi,arch,archfabric,archneo,neo,rei-api,rei-default,rei-fabric,jei-common,jei-neoforge,cloth,modmenu}`
- Merged jar for `javap -p`: find it with `ls .gradle/loom-cache/minecraftMaven/net/minecraft/*/26.2/*.jar` after the first build.
`grep` is ugrep (`grep -E`, quote `$`).

## Vanilla data/asset deltas 26.1.2 -> 26.2 (file-level diff; verify against codecs before scripting)
- loot_table: 1283/1355 changed, but sampled diffs only drop defaults (`bonus_rolls: 0.0`, `"add": false`) — likely no schema change.
- recipe: 598/1585 changed — sampled diffs drop `"category": "misc"`; 70 recipes added. Check `Recipe.CommonInfo`/`BookInfo` codecs.
- advancement 70 changed / 71 added; tags 45 changed / 37 added (1 removed); worldgen 73 changed / 12 added; villager_trade 28 changed.
- New registries: `sulfur_cube_archetype`; blockstates 64 changed / 28 added; models 294 added / 42 removed; items 16 changed / 31 added.
- `CriteriaTriggers` moved to `net.minecraft.advancements.triggers`; advancement predicates moved to `net.minecraft.advancements.predicates`.

## Report format
Files changed/added/deleted; remaining errors in your files; exact changes needed in files you do not own (AW lines with descriptors,
resource files, registry names). Commit on your branch before reporting. Never run the game, never push, never edit gradle files.
