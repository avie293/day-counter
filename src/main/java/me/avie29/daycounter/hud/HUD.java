package me.avie29.daycounter.hud;

import me.avie29.daycounter.getDayCount;
import me.avie29.daycounter.config.ModConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class HUD {

    public static void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();

        if (!ModConfig.hudVisible || client.player == null || client.font == null) {
            return;
        }

        Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
        int textWidth = client.font.width(text);
        int textHeight = client.font.lineHeight;

        int paddingX = 6;
        int paddingY = 4;

        int boxWidth = textWidth + (paddingX * 2);
        int boxHeight = textHeight + (paddingY * 2) + 1;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int x, y;

        if (ModConfig.useCustomPosition) {
            boolean screenSizeChanged = ModConfig.hudScreenWidth > 0
                && ModConfig.hudScreenHeight > 0
                && (ModConfig.hudScreenWidth != screenWidth || ModConfig.hudScreenHeight != screenHeight);

            if (screenSizeChanged) {
                ModConfig.hudX = Math.round((float) ModConfig.hudX * screenWidth / ModConfig.hudScreenWidth);
                ModConfig.hudY = Math.round((float) ModConfig.hudY * screenHeight / ModConfig.hudScreenHeight);
            }

            ModConfig.hudScreenWidth = screenWidth;
            ModConfig.hudScreenHeight = screenHeight;
            x = Math.max(0, Math.min(ModConfig.hudX, screenWidth - boxWidth));
            y = Math.max(0, Math.min(ModConfig.hudY, screenHeight - boxHeight));
            ModConfig.hudX = x;
            ModConfig.hudY = y;

            if (screenSizeChanged) {
                ModConfig.save();
            }
        } else {
            x = (screenWidth / 2) - (boxWidth / 2);
            y = screenHeight - 68;
        }

        int backgroundColor = 0x90000000;
        if (ModConfig.backgroundVisible) {
            guiGraphics.fill(x + 1, y, x + boxWidth - 1, y + 1, backgroundColor);
            guiGraphics.fill(x, y + 1, x + boxWidth, y + boxHeight - 1, backgroundColor);
            guiGraphics.fill(x + 1, y + boxHeight - 1, x + boxWidth - 1, y + boxHeight, backgroundColor);
        }

        int textX = x + paddingX;
        int textY = y + paddingY + 1;
        guiGraphics.text(client.font, text, textX, textY, 0xFFFFFFFF, true);
    }
}