package me.avie29.daycounter;

import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(DayCounter.MOD_ID)
public final class DayCounter {

    public static final String MOD_ID = "day_counter";

    public DayCounter(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        DayCounterClient.init(MOD_ID);

        IEventBus modBus = context.getModEventBus();
        modBus.addListener((RegisterKeyMappingsEvent event) -> event.register(KeyBindings.toggleHudKey));
        modBus.addListener((AddGuiOverlayLayersEvent event) ->
            event.getLayeredDraw().add(ResourceLocation.fromNamespaceAndPath(MOD_ID, "day_hud"), (graphics, deltaTracker) -> HUD.render(graphics)));

        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) ->
            event.getDispatcher().register(DayCounterClient.<CommandSourceStack>command(CommandSourceStack::sendSystemMessage)));
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                DayCounterClient.tick(Minecraft.getInstance());
            }
        });
    }
}
