package net.satisfy.vinery.neoforge.client;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.vinery.client.VineryClient;
import net.satisfy.vinery.client.gui.ApplePressGui;
import net.satisfy.vinery.client.gui.FermentationBarrelGui;
import net.satisfy.vinery.core.Vinery;
import net.satisfy.vinery.core.block.state.properties.VineryWoodType;
import net.satisfy.vinery.core.registry.ArmorRegistryClient;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.registry.ScreenhandlerTypeRegistry;
import java.util.function.Supplier;




@EventBusSubscriber(modid = Vinery.MOD_ID, value = Dist.CLIENT)
public class VineryClientNeoForge {

    @SubscribeEvent
    public static void onClientSetup(RegisterEvent event) {
        VineryClient.preInitClient();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        VineryClient.onInitializeClient();
        Sheets.addWoodType(VineryWoodType.DARK_CHERRY);
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Vinery.MOD_ID, "resourcepacks/bushy_leaves"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Bushy Leaves for Vinery"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }

    /**
     * The Winemaker set uses custom humanoid models. On NeoForge the armor layer keeps using the
     * {@code vinery:winemaker} equipment asset for the texture and only the model is swapped out here.
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(armorModelExtension(ArmorRegistryClient::getHatModel), ObjectRegistry.STRAW_HAT.get());
        event.registerItem(armorModelExtension(ArmorRegistryClient::getChestplateModel), ObjectRegistry.WINEMAKER_APRON.get());
        event.registerItem(armorModelExtension(ArmorRegistryClient::getLeggingsModel), ObjectRegistry.WINEMAKER_LEGGINGS.get());
        event.registerItem(armorModelExtension(ArmorRegistryClient::getBootsModel), ObjectRegistry.WINEMAKER_BOOTS.get());
    }

    private static IClientItemExtensions armorModelExtension(Supplier<? extends Model> model) {
        return new IClientItemExtensions() {
            @Override
            public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
                Model replacement = model.get();
                return replacement != null ? replacement : original;
            }
        };
    }

    /**
     * Registered here rather than through Architectury's {@code MenuScreenRegistry}: on NeoForge
     * {@code RegisterMenuScreensEvent} is posted before {@code FMLClientSetupEvent}, so a call made from
     * {@link VineryClient#onInitializeClient()} would come too late.
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ScreenhandlerTypeRegistry.APPLE_PRESS_GUI_HANDLER.get(), ApplePressGui::new);
        event.register(ScreenhandlerTypeRegistry.FERMENTATION_BARREL_GUI_HANDLER.get(), FermentationBarrelGui::new);
    }
}
