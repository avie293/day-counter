package me.avie29.daycounter;

import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = DayCounter.MOD_ID, dist = Dist.CLIENT)
public class DayCounter {

    public static final String MOD_ID = "day_counter";

    public DayCounter(IEventBus modEventBus) {
        DayCounterClient.init(MOD_ID);

        modEventBus.addListener((RegisterKeyMappingsEvent event) -> event.register(KeyBindings.toggleHudKey));
        modEventBus.addListener((RegisterGuiLayersEvent event) ->
            event.registerAboveAll(Identifier.fromNamespaceAndPath(MOD_ID, "day_hud"), (graphics, deltaTracker) -> HUD.render(graphics)));

        NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) ->
            event.getDispatcher().register(DayCounterClient.<CommandSourceStack>command(CommandSourceStack::sendSystemMessage)));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> DayCounterClient.tick(Minecraft.getInstance()));
    }
}
