package me.avie29.daycounter.hud;

import me.avie29.daycounter.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class KeyBindings {

    public static KeyMapping toggleHudKey;

    public static void register() {
        toggleHudKey = new KeyMapping(
            "key.daycounter.toggle_hud",
            72,
            KeyMapping.Category.MISC
        );
        KeyMappingHelper.registerKeyMapping(toggleHudKey);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
                var player = client.player;
                if (toggleHudKey != null && player != null) {
                while (toggleHudKey.consumeClick()) {
                    ModConfig.hudVisible = !ModConfig.hudVisible;
                    ModConfig.save();

                        player.sendSystemMessage(
                        Component.translatable(ModConfig.hudVisible
                            ? "message.daycounter.hud_enabled"
                            : "message.daycounter.hud_disabled")
                    );
                }
            }
        });
    }
}