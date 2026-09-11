# Vinery port: Minecraft 1.21.1 -> 1.21.10 (Architectury, Fabric + NeoForge)

You are one of several agents porting this mod in parallel. Each agent owns a DISJOINT set of files.
Do NOT edit files outside your assignment (if you must have a change in someone else's file,
describe it precisely in your final report instead; the coordinator will apply it).

## Environment / build

- Repo: an Architectury multi-project (`common`, `fabric`, `neoforge`). You work in your own git worktree
  (a full copy of the repo on your own branch). Commit your work on that branch when done.
- Mojang official mappings. Java 21. Gradle 8.14.3, Architectury Loom 1.13-SNAPSHOT (already configured).
- Compile check (run from the repo root of YOUR worktree). The first run in a fresh worktree takes several
  minutes (Loom sets up Minecraft); later runs are fast:

```
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home CURSEFORGE_API_KEY=x
./gradlew :common:compileJava --continue -q 2>&1 | grep -E "error:|symbol:|location:" | grep -E "<paths you own>" 
```
  Other agents' files will still be broken, so filter to your own paths (e.g. `grep -E "core/block/|core/registry/"`).
  The whole module only compiles once everyone is done, so "no errors in my files" is the finish line.
  `:fabric:compileJava` / `:neoforge:compileJava` cannot succeed until `common` compiles; for fabric/neoforge
  files, reason carefully from the reference sources below and keep the platform code minimal.
- javac is configured with `-Xmaxerrs 5000` so you see everything.
- Never run the game, never run `git push`.

## Reference sources (READ THESE instead of guessing; all decompiled / extracted, grep-able)

- Minecraft 1.21.10 (Mojang mapped, decompiled):  `/private/tmp/claude-501/-Users-alextodd-temp-Github-NOTSYNCED-Vinery/0be9815f-f3f4-4135-a2e2-0a80aae3a5d1/scratchpad/mc1.21.10/net/minecraft`
- Minecraft 1.21.10 vanilla data + assets (json):  `.../scratchpad/mcres1.21.10/{data,assets}/minecraft`
- NeoForge 21.10.64 sources: `.../scratchpad/neo/net/neoforged`
- Fabric API 0.138.4 sources: `.../scratchpad/fapi/net/fabricmc/fabric`
- Architectury API 18.0.8: common `.../scratchpad/arch18`, fabric impl `.../scratchpad/archfabric`, neoforge impl `.../scratchpad/archneo`
- REI 21.9.813 API + default plugin: `.../scratchpad/rei/me/shedaniel/rei`
- JEI 26.3.0.31 API: `.../scratchpad/jei/mezz/jei/api`; full JEI (neoforge) sources: `.../scratchpad/jeifull/mezz/jei`
- ModMenu 16.0.1: `.../scratchpad/modmenu`
(`...` = `/private/tmp/claude-501/-Users-alextodd-temp-Github-NOTSYNCED-Vinery/0be9815f-f3f4-4135-a2e2-0a80aae3a5d1/scratchpad`)

Tip: `grep` on this machine is ugrep; use `grep -E` / `grep -nE` and avoid `\(`-style BRE alternation.
When unsure about a signature, open the vanilla class. Vanilla subclasses (e.g. `ShelfRenderer`, `CampfireRenderer`,
`BoatRenderer`, `SignRenderer`, `LlamaRenderer`, `WanderingTraderRenderer`, `SweetBerryBushBlock`, `BarrelBlock`,
`ChestBlockEntity`, `AbstractFurnaceBlockEntity`, `ShapedRecipe`, `Boat`) are the best templates.

## Already done globally (do not redo)

Mechanical renames were already applied to every Java file:
`ItemInteractionResult` -> `InteractionResult` (PASS_TO_DEFAULT_BLOCK_INTERACTION -> TRY_WITH_EMPTY_HAND,
sidedSuccess(..) -> SUCCESS), `InteractionResultHolder<ItemStack>` -> `InteractionResult`
(`success(stack)` -> `InteractionResult.SUCCESS.heldItemTransformedTo(stack)`), MobEffects renames
(JUMP->JUMP_BOOST, HEAL->INSTANT_HEALTH, HARM->INSTANT_DAMAGE, DAMAGE_RESISTANCE->RESISTANCE, DIG_SPEED->HASTE,
DAMAGE_BOOST->STRENGTH), `noCollission()`->`noCollision()`, `MobSpawnType`->`EntitySpawnReason`,
`getMaxBuildHeight()`->`getMaxY()`, `registryOrThrow`->`lookupOrThrow`, `UseAnim`->`ItemUseAnimation`,
`ArmorItem.Type`->`net.minecraft.world.item.equipment.ArmorType`, `getCommandSenderWorld()`->`level()`,
`FastColor.ARGB32`->`net.minecraft.util.ARGB`, `DirectionProperty`->`EnumProperty<Direction>`.
The access widener `common/src/main/resources/vinery.accesswidener` already exposes the `BlockEntityType` constructor.

## Cross-cutting decisions (everyone must follow)

### Registry ids are mandatory (1.21.2+)
`new Item(new Item.Properties())` throws at runtime unless `Item.Properties.setId(ResourceKey<Item>)` was called;
same for `BlockBehaviour.Properties.setId(ResourceKey<Block>)`. `ofFullCopy` does NOT copy the id.
BlockItems additionally need `.useBlockDescriptionPrefix()`.
The registry owner (core/registry + core/util agent) adds helpers in `net.satisfy.vinery.core.util.GeneralUtil`:
```java
public static ResourceKey<Block> blockKey(String name)   // ResourceKey.create(Registries.BLOCK, Vinery.identifier(name))
public static ResourceKey<Item>  itemKey(String name)
public static BlockBehaviour.Properties blockProps(String name)                 // Properties.of().setId(blockKey(name))
public static BlockBehaviour.Properties blockProps(String name, Block copyFrom) // Properties.ofFullCopy(copyFrom).setId(...)
public static Item.Properties itemProps(String name)                            // new Item.Properties().setId(itemKey(name))
```
Everything that constructs a Block or Item MUST route its Properties through these (or call setId explicitly).
Other agents: if your code constructs Items/Blocks (e.g. platform helpers, spawn eggs), call setId yourself with
`ResourceKey.create(Registries.ITEM, Vinery.identifier(name))`.

### Entity types
`EntityType.Builder.build(String)` is now `build(ResourceKey<EntityType<?>>)`:
`ResourceKey.create(Registries.ENTITY_TYPE, Vinery.identifier(name))`.
`BlockEntityType.Builder` is gone: use `new BlockEntityType<>(Factory::new, Set.of(blocks...))` (constructor widened by our AW).

### Interaction / block API (net.minecraft.world.level.block.state.BlockBehaviour)
- `useItemOn(ItemStack, BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)` returns `InteractionResult`
  (return `TRY_WITH_EMPTY_HAND` to fall through to `useWithoutItem`).
- `onRemove(state, level, pos, newState, moved)` no longer exists. Container drop logic goes into
  `protected void affectNeighborsAfterRemoval(BlockState, ServerLevel, BlockPos, boolean)` (see `BarrelBlock`/`ChestBlock`)
  and/or `Containers.dropContentsOnDestroy(state, newState, level, pos)`; block-entity-removal side effects that must
  run on both sides go in `BlockEntity.preRemoveSideEffects(BlockPos, BlockState)`.
- `updateShape(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource)`.
- `neighborChanged(BlockState, Level, BlockPos, Block, @Nullable Orientation, boolean)`.
- `randomTick`/`tick` take `ServerLevel`. `getCloneItemStack(LevelReader, BlockPos, BlockState, boolean)`.
- `playerWillDestroy` returns BlockState. `getDrops(BlockState, LootParams.Builder)`.
- Every Block subclass must implement `protected MapCodec<? extends Block> codec()` (use `simpleCodec(Ctor::new)` when
  the ctor is `(Properties)`, otherwise build a `RecordCodecBuilder.mapCodec`).
- `BushBlock` is now `VegetationBlock`. `Level.getGameRules()` -> only on `ServerLevel` (`serverLevel.getGameRules()`).
- `BlockPlaceContext`, `LevelAccessor.updateNeighborsAt(pos, block)` exist; `blockUpdated` removed.

### Item API (net.minecraft.world.item.Item)
- `use(Level, Player, InteractionHand)` returns `InteractionResult`.
- `appendHoverText(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`.
- `inventoryTick(ItemStack, ServerLevel, Entity, @Nullable EquipmentSlot)`.
- Food: `Item.Properties.food(FoodProperties)` or `.food(FoodProperties, Consumable)`. `FoodProperties` is now
  `(nutrition, saturation, canAlwaysEat)`; effects live in a `Consumable` (`Consumables.defaultDrink()/defaultFood()`
  `.onConsume(new ApplyStatusEffectsConsumeEffect(...))`). `FoodProperties.PossibleEffect` is gone.
- `Item.Properties.usingConvertsTo(Item)` replaces returning a bottle manually; `craftRemainder` still exists.
- Armor: `ArmorItem` is gone. Armor is `new Item(props.humanoidArmor(ArmorMaterial, ArmorType))` where
  `ArmorMaterial` is the record in `net.minecraft.world.item.equipment` (durability, defense map, enchantValue,
  equipSound, toughness, kbResist, repair TagKey<Item>, `ResourceKey<EquipmentAsset>`). Equipment textures are data
  driven: `assets/vinery/equipment/<asset>.json` referencing `textures/entity/equipment/humanoid/<tex>.png`.
- Spawn eggs: `new SpawnEggItem(props.spawnEgg(entityType))` (Architectury's `ArchitecturySpawnEggItem` is gone).
  Spawn egg colors are no longer in code (item model `minecraft:spawn_egg`? no: 1.21.10 uses a plain textured model,
  see vanilla `assets/minecraft/items/pig_spawn_egg.json` + `models/item/pig_spawn_egg.json`).
- Boat items: `new BoatItem(EntityType<? extends AbstractBoat>, props)`.
- Sign items: `SignItem(Block standing, Block wall, Item.Properties)`; `HangingSignItem(Block, Block, Item.Properties)`.
- `BlockItem` unchanged; `ItemNameBlockItem` is gone (use `BlockItem` + `useItemDescriptionPrefix()`).

### Effects (MobEffect)
- `applyEffectTick(ServerLevel, LivingEntity, int amplifier)` returns boolean; `onEffectStarted(LivingEntity, int)`.

### Entities
- Save data uses `ValueInput`/`ValueOutput` (`net.minecraft.world.level.storage`) instead of CompoundTag in
  `readAdditionalSaveData`/`addAdditionalSaveData` (`input.getIntOr("key", 0)`, `output.putInt`, `input.read("key", CODEC)`).
- `Entity.moveTo(double,double,double,float,float)`; `Entity.hurtServer(ServerLevel, DamageSource, float)`.
- `Mob.finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, @Nullable SpawnGroupData)`.
- Boats: `Boat`/`ChestBoat` are concrete classes taking `(EntityType, Level, Supplier<Item>)`; each wood needs its own
  `EntityType` built with `EntityType.Builder.of((type, level) -> new Boat(type, level, () -> ITEM.get()), MobCategory.MISC)
  .noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10).build(key)`.
  Delete `DarkCherryBoatEntity`/`DarkCherryChestBoatEntity` in favour of vanilla `Boat`/`ChestBoat`; the renderer is
  vanilla `BoatRenderer(context, ModelLayerLocation)` whose texture is derived from the layer location
  (`ModelLayers.createBoatModelName`-style: layer `vinery:boat/dark_cherry` -> `textures/entity/boat/dark_cherry.png`).

### Block entities
- `saveAdditional(ValueOutput)` / `loadAdditional(ValueInput)`; `ContainerHelper.saveAllItems(ValueOutput, NonNullList)`,
  `ContainerHelper.loadAllItems(ValueInput, NonNullList)`; `getUpdateTag(HolderLookup.Provider)` still returns CompoundTag
  (use `saveCustomOnly(provider)` / `saveWithoutMetadata`).
- `RecipeManager` is server-only: `serverLevel.recipeAccess()` / `server.getRecipeManager().getRecipeFor(type, input, level)`.

### Recipes
- `Recipe<T>`: `matches(T, Level)`, `assemble(T, HolderLookup.Provider)`, `getSerializer()`, `getType()`,
  `placementInfo()`, `recipeBookCategory()` (register a custom `RecipeBookCategory` via
  `DeferredRegister<RecipeBookCategory>` on `Registries.RECIPE_BOOK_CATEGORY`, or reuse a vanilla one), no more
  `getIngredients()`/`getResultItem()`/`canCraftInDimensions`.
- `RecipeSerializer<T>`: `MapCodec<T> codec()` + `StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()`.
- `Ingredient`: `Ingredient.of(ItemLike...)`, `Ingredient.CODEC` (a string/tag holder set: `"minecraft:apple"` or `"#tag"`),
  `Ingredient.CONTENTS_STREAM_CODEC`, `test(ItemStack)`, `items()` (Stream<Holder<Item>>), `isEmpty()`.
- Clients no longer receive recipes. REI: use the COMMON plugin (`me.shedaniel.rei.api.common.plugins.REICommonPlugin`, Fabric entrypoint key `rei_common`, NeoForge: annotate with `@REIPluginCommon` / see how REI discovers NeoForge plugins in `archneo`-style: grep `REIPluginLoader`/`@REIPluginCommon` in `.../scratchpad/rei`;
  `registerDisplays(ServerDisplayRegistry)` + `beginRecipeFiller(Class).filterType(TYPE).fill(...)`, and
  `registerDisplaySerializer(DisplaySerializerRegistry)`) and only categories on the client (see REI DefaultPlugin).
  JEI: sync the mod recipes ourselves (Architectury `NetworkManager` packet on player join + datapack reload) into a
  client cache, and register from that cache. Fallback for both: the client cache.

### Client rendering (1.21.9+ "submit" model)
- `BlockEntityRenderer<T, S extends BlockEntityRenderState>`: implement `createRenderState()`,
  `extractRenderState(T, S, float partialTick, Vec3 cameraPos, CrumblingOverlay)` (copy everything you need from the BE
  into S; the BE is NOT available in submit), and
  `submit(S, PoseStack, SubmitNodeCollector, CameraRenderState)`. See vanilla `ShelfRenderer`, `CampfireRenderer`,
  `BannerRenderer`, `AbstractSignRenderer`.
  - Items: `context.itemModelResolver().updateForTopItem(itemStackRenderState, stack, ItemDisplayContext.FIXED, level, null, seed)`
    then `itemStackRenderState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, outlineColor)`.
  - Blocks: `submitNodeCollector.submitBlock(poseStack, blockState, light, overlay, outlineColor)` (check the exact
    signature in `SubmitNodeCollector.java`).
  - Models: `submitNodeCollector.submitModel(model, state, poseStack, renderType, light, overlay, outlineColor, sprite, crumbling)`;
    `Model.Simple(ModelPart, RenderType::entityCutoutNoCull)` for plain parts. Per-part visibility must be decided in
    extract/submit before submitting (mutating shared ModelParts is fine on the render thread).
- Entities: `EntityRenderer<T, S extends EntityRenderState>` with `createRenderState`/`extractRenderState`/`submit`.
  `getTextureLocation(S)` takes the render state. `MobRenderer<T, S, M extends EntityModel<S>>`.
  `EntityModel<S>` subclasses implement `setupAnim(S)`; `ModelPart.copyFrom` is gone (copy x/y/z/xRot/yRot/zRot manually).
- Vanilla `SignRenderer`/`HangingSignRenderer` already cover every registered `WoodType` (they iterate `WoodType.values()`),
  so register them directly for our sign block entity types and delete the custom sign renderers.
- Block render layers: `RenderTypeRegistry.register(ChunkSectionLayer.CUTOUT, blocks...)` (Architectury 18;
  `net.minecraft.client.renderer.chunk.ChunkSectionLayer`).
- GUI: `GuiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, w, h, texW, texH)` (`net.minecraft.client.renderer.RenderPipelines`).
  `AbstractContainerScreen.renderBg(GuiGraphics, float, int, int)`; `renderLabels(GuiGraphics, int, int)`.
- Armor with custom models: Fabric `net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer`
  (`render(PoseStack, SubmitNodeCollector, ItemStack, HumanoidRenderState, EquipmentSlot, int light, HumanoidModel<HumanoidRenderState> contextModel)`);
  NeoForge `IClientItemExtensions.getHumanoidArmorModel(ItemStack, EquipmentClientInfo.LayerType, Model original)` registered
  via `RegisterClientExtensionsEvent`. Custom armor models should extend `HumanoidModel<HumanoidRenderState>`.
- Screen factories: `dev.architectury.registry.client.gui.MenuScreenRegistry.registerScreenFactory(menuType, Screen::new)`
  (moved out of `MenuRegistry` in Architectury 18).
- `SubmitNodeCollector` methods (see `OrderedSubmitNodeCollector.java`): `submitBlock(PoseStack, BlockState, light, overlay, outlineColor)`,
  `submitModelPart(ModelPart, PoseStack, RenderType, light, overlay, @Nullable sprite)`, `submitModel(...)`, `submitText(...)`,
  `submitItem(...)`, `submitCustomGeometry(PoseStack, RenderType, CustomGeometryRenderer)`.
- `ColorHandlerRegistry.registerItemColors` is gone (item tint is data-driven via item model `tints`); keep block colors.

### Resources (data/assets)
- Pack formats for 1.21.10: resource 69, data 88 (`pack.mcmeta`).
- Every item needs `assets/vinery/items/<item>.json` (`{"model":{"type":"minecraft:model","model":"vinery:item/<item>"}}`
  or `vinery:block/<block>`); item model overrides/predicates are gone.
- Recipe ingredients are plain strings: `"minecraft:apple"` / `"#minecraft:planks"` (no `{"item":...}` objects);
  `result` is `{"id":..., "count":...}`.
- Loot tables / advancements / tags: compare with vanilla 1.21.10 json when in doubt.

## Report format
End with: the list of files you changed/added/deleted, anything you could not finish, and any change you need
applied in files you do not own (exact code). Commit on your branch with a clear message before reporting.
- REI NeoForge sources (plugin annotations `@REIPluginCommon`/`@REIPluginClient`): `.../scratchpad/reineo`; REI Fabric impl: `.../scratchpad/reifabric` (Fabric entrypoints: rei_common, rei_client, rei_server).
