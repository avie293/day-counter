package me.avie29.daycounter;

import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class DayCounter implements ClientModInitializer {

    public static final String MOD_ID = "day-counter";

    @Override
    public void onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(KeyBindings.toggleHudKey);
        DayCounterClient.init(MOD_ID);

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "day_hud"), (graphics, deltaTracker) -> HUD.render(graphics));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(DayCounterClient.<FabricClientCommandSource>command(FabricClientCommandSource::sendFeedback)));

        ClientTickEvents.END_CLIENT_TICK.register(DayCounterClient::tick);
    }
}
