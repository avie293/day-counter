package cc.avie.daycounter.hud;

import com.mojang.blaze3d.platform.InputConstants;
import cc.avie.daycounter.config.DayCounterConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class KeyBindings {
    public static final KeyMapping TOGGLE_HUD = new KeyMapping(
        "key.daycounter.toggle_hud",
        InputConstants.KEY_H,
        KeyMapping.Category.MISC
    );

    private KeyBindings() {
    }

    public static void handle(Minecraft client) {
        var player = client.player;
        if (player == null) {
            return;
        }
        while (TOGGLE_HUD.consumeClick()) {
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
