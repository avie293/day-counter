package me.avie29.daycounter.hud;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyMapping toggleHudKey;

    public static void register(RegisterKeyMappingsEvent event) {
        toggleHudKey = new KeyMapping(
            "key.daycounter.toggle_hud",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.misc"
        );
        event.register(toggleHudKey);
    }
}