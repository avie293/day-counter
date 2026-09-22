package me.avie29.daycounter.hud;

import me.avie29.daycounter.config.ModConfig;
import me.avie29.daycounter.getDayCount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class HUD {

    public static void render(ForgeGui forgeGui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        if (!ModConfig.hudVisible || client.player == null || client.font == null) {
            return;
        }

        Component text = Component.translatable("day_counter.hud.day", getDayCount.getCurrentDay());
        int textWidth = client.font.width(text);
        int textHeight = client.font.lineHeight;
        int paddingX = 6;
        int paddingY = 4;
        int boxWidth = textWidth + (paddingX * 2);
        int boxHeight = textHeight + (paddingY * 2) + 1;
        int x;
        int y;

        if (ModConfig.useCustomPosition) {
            boolean screenSizeChanged = ModConfig.hudScreenWidth > 0 && ModConfig.hudScreenHeight > 0
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

        if (ModConfig.backgroundVisible) {
            guiGraphics.fill(x + 1, y, x + boxWidth - 1, y + 1, 0x90000000);
            guiGraphics.fill(x, y + 1, x + boxWidth, y + boxHeight - 1, 0x90000000);
            guiGraphics.fill(x + 1, y + boxHeight - 1, x + boxWidth - 1, y + boxHeight, 0x90000000);
        }
        guiGraphics.drawString(client.font, text, x + paddingX, y + paddingY + 1, 0xFFFFFFFF, true);
    }
}
