package cc.avie.daycounter.fabric;

import cc.avie.daycounter.DayCounterClient;
import cc.avie.daycounter.hud.HUD;
import cc.avie.daycounter.hud.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class DayCounterFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(KeyBindings.toggleHudKey);
        DayCounterClient.init(DayCounterClient.MOD_ID);

        HudElementRegistry.addLast(DayCounterClient.id("day_hud"), (graphics, deltaTracker) -> HUD.render(graphics));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(DayCounterClient.<FabricClientCommandSource>command(FabricClientCommandSource::sendFeedback)));

        ClientTickEvents.END_CLIENT_TICK.register(DayCounterClient::tick);
    }
}
