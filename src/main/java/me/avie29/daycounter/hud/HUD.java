package me.avie29.daycounter.hud;

import me.avie29.daycounter.getDayCount;
import me.avie29.daycounter.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class HUD {

    public static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!ModConfig.hudVisible || client.player == null || client.textRenderer == null) {
            return;
        }

        Text text = Text.translatable("hud.day", getDayCount.getCurrentDay());
        int textWidth = client.textRenderer.getWidth(text);
        int textHeight = client.textRenderer.fontHeight;

        int paddingX = 6;
        int paddingY = 4;

        int boxWidth = textWidth + (paddingX * 2);
        int boxHeight = textHeight + (paddingY * 2) + 1;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        ModConfig.updatePositionForScreen(screenWidth, screenHeight, boxWidth, boxHeight);

        int x, y;

        if (ModConfig.useCustomPosition) {
            x = ModConfig.hudX;
            y = ModConfig.hudY;
        } else {
            x = (screenWidth / 2) - (boxWidth / 2);
            y = screenHeight - 68;
        }

        int backgroundColor = 0x90000000;
        if (ModConfig.backgroundVisible) {
            drawContext.fill(x + 1, y, x + boxWidth - 1, y + 1, backgroundColor);
            drawContext.fill(x, y + 1, x + boxWidth, y + boxHeight - 1, backgroundColor);
            drawContext.fill(x + 1, y + boxHeight - 1, x + boxWidth - 1, y + boxHeight, backgroundColor);
        }

        int textX = x + paddingX;
        int textY = y + paddingY + 1;
        drawContext.drawTextWithShadow(client.textRenderer, text, textX, textY, 0xFFFFFFFF);
    }
}