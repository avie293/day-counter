package me.avie29.daycounter.hud;

import me.avie29.daycounter.config.ModConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public class KeyBindings {

    public static KeyMapping toggleHudKey;

    public static void register(RegisterKeyMappingsEvent event) {
        toggleHudKey = new KeyMapping(
            "key.day_counter.toggle_hud",
            72,
            KeyMapping.CATEGORY_MISC
        );
        event.register(toggleHudKey);
    }

    public static void handleClientTick(Minecraft client) {
        var player = client.player;
        if (toggleHudKey != null && player != null) {
                while (toggleHudKey.consumeClick()) {
                    ModConfig.hudVisible = !ModConfig.hudVisible;
                    ModConfig.save();

                        player.sendSystemMessage(
                        Component.translatable(ModConfig.hudVisible
                            ? "day_counter.message.hud_enabled"
                            : "day_counter.message.hud_disabled")
                    );
                }
        }
    }
}