package me.avie29.daycounter.hud;

import com.mojang.blaze3d.platform.InputConstants;
import me.avie29.daycounter.config.DayCounterConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class KeyBindings {

    public static final KeyMapping toggleHudKey = new KeyMapping(
        "key.daycounter.toggle_hud",
        InputConstants.KEY_H,
        "key.categories.misc"
    );

    public static void handle(Minecraft client) {
        var player = client.player;
        if (player == null) {
            return;
        }
        while (toggleHudKey.consumeClick()) {
            boolean visible = !DayCounterConfig.HUD_VISIBLE.get();
            DayCounterConfig.HUD_VISIBLE.set(visible);
            DayCounterConfig.get().save();

            player.sendSystemMessage(
                Component.translatable(visible
                    ? "message.daycounter.hud_enabled"
                    : "message.daycounter.hud_disabled")
            );
        }
    }
}
