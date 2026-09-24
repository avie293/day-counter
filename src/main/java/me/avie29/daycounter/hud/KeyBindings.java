package me.avie29.daycounter.hud;

import me.avie29.daycounter.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyBinding toggleHudKey;

    public static void register() {
        toggleHudKey = new KeyBinding(
            "key.daycounter.toggle_hud",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.misc"
        );
        KeyBindingHelper.registerKeyBinding(toggleHudKey);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
                var player = client.player;
                if (toggleHudKey != null && player != null) {
                while (toggleHudKey.wasPressed()) {
                    ModConfig.hudVisible = !ModConfig.hudVisible;
                    ModConfig.save();

                        player.sendMessage(
                        Text.translatable(ModConfig.hudVisible
                            ? "message.daycounter.hud_enabled"
                            : "message.daycounter.hud_disabled"), false);
                }
            }
        });
    }
}