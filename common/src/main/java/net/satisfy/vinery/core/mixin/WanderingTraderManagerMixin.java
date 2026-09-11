package net.satisfy.vinery.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.satisfy.vinery.core.entity.TraderMuleEntity;
import net.satisfy.vinery.core.registry.EntityTypeRegistry;
import net.satisfy.vinery.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderManagerMixin implements CustomSpawner {
	@Shadow @Final private ServerLevelData serverLevelData;

	@Inject(method = "spawn", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;"), cancellable = true)
	private void trySpawn(ServerLevel world, CallbackInfoReturnable<Boolean> cir) {
		if (world.random.nextDouble() < PlatformHelper.getTraderSpawnChance()) {
			ServerPlayer playerEntity = world.getRandomPlayer();
			if (playerEntity != null) {
				BlockPos blockPos = playerEntity.blockPosition();
				PoiManager pointOfInterestStorage = world.getPoiManager();
				Optional<BlockPos> optional = pointOfInterestStorage.find(
						type -> type.is(PoiTypes.MEETING),
						pos -> true,
						blockPos,
						48,
						PoiManager.Occupancy.ANY
				);
				BlockPos blockPos2 = optional.orElse(blockPos);
				BlockPos blockPos3 = this.vinery$findSpawnPositionNear(world, blockPos2, 48);
				if (blockPos3 != null && this.vinery$hasEnoughSpace(world, blockPos3)) {
					var biome = world.getBiome(blockPos3);
					if (biome != null && !biome.is(Biomes.THE_VOID)) {
						var wanderingWinemakerType = EntityTypeRegistry.WANDERING_WINEMAKER.get();
						if (wanderingWinemakerType != null) {
							WanderingTrader wanderingTraderEntity = wanderingWinemakerType.spawn(world, blockPos3, EntitySpawnReason.EVENT);
							if (wanderingTraderEntity != null) {
								if (PlatformHelper.shouldSpawnWithMules()) {
									for (int j = 0; j < 2; ++j) {
										BlockPos blockPos4 = this.vinery$findSpawnPositionNear(world, wanderingTraderEntity.blockPosition(), 4);
										if (blockPos4 != null) {
											var muleType = EntityTypeRegistry.MULE.get();
											if (muleType != null) {
												TraderMuleEntity traderMuleEntity = muleType.spawn(world, blockPos4, EntitySpawnReason.EVENT);
												if (traderMuleEntity != null) {
													traderMuleEntity.setLeashedTo(wanderingTraderEntity, true);
												}
											}
										}
									}
								}
								if (this.serverLevelData != null) {
									this.serverLevelData.setWanderingTraderId(wanderingTraderEntity.getUUID());
									wanderingTraderEntity.setDespawnDelay(PlatformHelper.getTraderSpawnDelay());
									wanderingTraderEntity.setWanderTarget(blockPos2);
									wanderingTraderEntity.setHomeTo(blockPos2, 16);
									cir.setReturnValue(true);
								}
							}
						}
					}
				}
			}
		}
	}

	@Unique
	@Nullable
	private BlockPos vinery$findSpawnPositionNear(ServerLevel world, BlockPos pos, int range) {
		BlockPos found = null;
		SpawnPlacementType spawnPlacementType = SpawnPlacements.getPlacementType(EntityType.WANDERING_TRADER);

		for (int i = 0; i < 10; ++i) {
			int x = pos.getX() + world.random.nextInt(range * 2) - range;
			int z = pos.getZ() + world.random.nextInt(range * 2) - range;
			int y = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
			BlockPos candidate = new BlockPos(x, y, z);
			if (spawnPlacementType.isSpawnPositionOk(world, candidate, EntityType.WANDERING_TRADER)) {
				found = candidate;
				break;
			}
		}

		return found;
	}

	@Unique
	private boolean vinery$hasEnoughSpace(BlockGetter world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.betweenClosed(pos, pos.offset(1, 2, 1))) {
			if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
