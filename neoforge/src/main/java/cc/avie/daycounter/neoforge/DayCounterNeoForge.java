package cc.avie.daycounter.neoforge;

import cc.avie.daycounter.DayCounterClient;
import cc.avie.daycounter.hud.HUD;
import cc.avie.daycounter.hud.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = "day_counter", dist = Dist.CLIENT)
public class DayCounterNeoForge {

    public DayCounterNeoForge(IEventBus modBus, ModContainer container) {
        // NeoForge mod ids can't contain "-", TabbyLib needs the id of the mod on this loader
        DayCounterClient.init(container.getModId());

        modBus.addListener(RegisterKeyMappingsEvent.class, event -> event.register(KeyBindings.toggleHudKey));
        modBus.addListener(RegisterGuiLayersEvent.class, event ->
            event.registerAboveAll(DayCounterClient.id("day_hud"), (graphics, deltaTracker) -> HUD.render(graphics)));

        NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent.class, event ->
            event.getDispatcher().register(DayCounterClient.<CommandSourceStack>command(CommandSourceStack::sendSystemMessage)));

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> DayCounterClient.tick(Minecraft.getInstance()));
    }
}
