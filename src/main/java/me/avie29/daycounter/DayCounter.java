package me.avie29.daycounter;

import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DayCounter implements ClientModInitializer {

    public static final String MOD_ID = "day-counter";

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(KeyBindings.toggleHudKey);
        DayCounterClient.init(MOD_ID);

        HudRenderCallback.EVENT.register((graphics, deltaTracker) -> HUD.render(graphics));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(DayCounterClient.<FabricClientCommandSource>command(FabricClientCommandSource::sendFeedback)));

        ClientTickEvents.END_CLIENT_TICK.register(DayCounterClient::tick);
    }
}
