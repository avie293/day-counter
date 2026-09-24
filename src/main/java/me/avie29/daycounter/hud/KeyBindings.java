package me.avie29.daycounter.hud;

import com.mojang.blaze3d.platform.InputConstants;
import me.avie29.daycounter.config.ModConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyMapping toggleHudKey;

    public static void register(RegisterKeyMappingsEvent event) {
        toggleHudKey = new KeyMapping(
            "key.daycounter.toggle_hud",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            KeyMapping.Category.MISC
        );
        event.register(toggleHudKey);

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post tickEvent) -> {
            var client = net.minecraft.client.Minecraft.getInstance();
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