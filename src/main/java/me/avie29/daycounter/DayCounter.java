package me.avie29.daycounter;

import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(DayCounter.MOD_ID)
public final class DayCounter {

    public static final String MOD_ID = "day_counter";

    public DayCounter() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        DayCounterClient.init(MOD_ID);

        RegisterKeyMappingsEvent.BUS.addListener(event -> event.register(KeyBindings.toggleHudKey));
        AddGuiOverlayLayersEvent.BUS.addListener(event ->
            event.getLayeredDraw().add(Identifier.fromNamespaceAndPath(MOD_ID, "day_hud"), (graphics, deltaTracker) -> HUD.render(graphics)));

        RegisterClientCommandsEvent.BUS.addListener(event ->
            event.getDispatcher().register(DayCounterClient.<CommandSourceStack>command(CommandSourceStack::sendSystemMessage)));
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> DayCounterClient.tick(Minecraft.getInstance()));
    }
}
