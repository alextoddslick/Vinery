package net.satisfy.vinery.core.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.util.VillagerUtil;

public class WanderingWinemakerEntity extends WanderingTrader {

	public WanderingWinemakerEntity(EntityType<? extends WanderingWinemakerEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Override
	protected void updateTrades(ServerLevel level) {
		MerchantOffers offers = this.getOffers();
		this.addOffersFromTradeSet(level, offers, VillagerUtil.WANDERING_WINEMAKER_COMMON);
	}
}
