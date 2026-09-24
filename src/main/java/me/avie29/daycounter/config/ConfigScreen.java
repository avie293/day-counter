package me.avie29.daycounter.config;

import me.avie29.daycounter.getDayCount;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

    private static final int SNAP_DISTANCE = 8;
    private static final int GRID_SIZE = 10;

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private boolean showVerticalGuide = false;
    private boolean showHorizontalGuide = false;

    public ConfigScreen() {
        super(Component.translatable("screen.daycounter.config.title"));
    }

    @Override
    protected void init() {
        if (!ModConfig.useCustomPosition) {
            setDefaultPosition();
            ModConfig.useCustomPosition = true;
        }

        int buttonWidth = 140;
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("button.daycounter.reset_position"),
                button -> resetPosition()
            )
            .bounds((this.width - buttonWidth) / 2, this.height - 30, buttonWidth, 20)
            .build()
        );

    }

    private void setDefaultPosition() {
        Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
        int boxWidth = this.font.width(text) + 12;

        ModConfig.hudX = (this.width / 2) - (boxWidth / 2);
        ModConfig.hudY = this.height - 68;
        ModConfig.hudScreenWidth = this.width;
        ModConfig.hudScreenHeight = this.height;
        ModConfig.hudAnchorInitialized = false;
    }

    private void resetPosition() {
        setDefaultPosition();
        ModConfig.useCustomPosition = true;
        ModConfig.save();
    }

    @Override
    public void renderBackground(GuiGraphics drawContext, int mouseX, int mouseY, float partialTick) {
        drawContext.fill(0, 0, this.width, this.height, 0x90000000);
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float partialTick) {
        super.render(drawContext, mouseX, mouseY, partialTick);

        Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
        int textWidth = this.font.width(text);
        int textHeight = this.font.lineHeight;
        int paddingX = 6;
        int paddingY = 4;
        int boxWidth = textWidth + (paddingX * 2);
        int boxHeight = textHeight + (paddingY * 2) + 1;

        ModConfig.updatePositionForScreen(this.width, this.height, boxWidth, boxHeight);

        if (this.isDragging) {
            int rawX = mouseX - this.dragOffsetX;
            int rawY = mouseY - this.dragOffsetY;
            int maxX = this.width - boxWidth;
            int maxY = this.height - boxHeight;

            ModConfig.hudX = snapCoordinate(rawX, maxX, (this.width - boxWidth) / 2, true);
            ModConfig.hudY = snapCoordinate(rawY, maxY, (this.height - boxHeight) / 2, false);

            ModConfig.hudX = Math.max(0, Math.min(ModConfig.hudX, this.width - boxWidth));
            ModConfig.hudY = Math.max(0, Math.min(ModConfig.hudY, this.height - boxHeight));
        } else {
            this.showVerticalGuide = false;
            this.showHorizontalGuide = false;
        }

        int x = ModConfig.hudX;
        int y = ModConfig.hudY;

        if (this.isDragging) {
            if (this.showVerticalGuide) {
                int guideX = x + (boxWidth / 2);
                drawContext.fill(guideX, 0, guideX + 1, this.height, 0x8033CCFF);
            }

            if (this.showHorizontalGuide) {
                int guideY = y + (boxHeight / 2);
                drawContext.fill(0, guideY, this.width, guideY + 1, 0x8033CCFF);
            }
        }

        if (ModConfig.backgroundVisible) {
            drawContext.fill(x + 1, y, x + boxWidth - 1, y + 1, 0x90000000);
            drawContext.fill(x, y + 1, x + boxWidth, y + boxHeight - 1, 0x90000000);
            drawContext.fill(x + 1, y + boxHeight - 1, x + boxWidth - 1, y + boxHeight, 0x90000000);
        }

        drawContext.fill(x + 1, y, x + boxWidth - 1, y + 1, 0xFFFFFF00);
        drawContext.fill(x + 1, y + boxHeight - 1, x + boxWidth - 1, y + boxHeight, 0xFFFFFF00);
        drawContext.fill(x, y + 1, x + 1, y + boxHeight - 1, 0xFFFFFF00);
        drawContext.fill(x + boxWidth - 1, y + 1, x + boxWidth, y + boxHeight - 1, 0xFFFFFF00);

        drawContext.drawString(this.font, text, x + paddingX, y + paddingY + 1, 0xFFFFFFFF);

        Component hint1 = Component.translatable("screen.daycounter.config.hint_snap");
        Component hint2 = Component.translatable("screen.daycounter.config.hint_controls");
        drawContext.drawString(this.font, hint1, (this.width / 2) - (this.font.width(hint1) / 2), 20, 0xFFFFFFFF);
        drawContext.drawString(this.font, hint2, (this.width / 2) - (this.font.width(hint2) / 2), 32, 0xAAAAAAFF);

    }

    private int snapCoordinate(int value, int max, int center, boolean vertical) {
        int clampedValue = Math.max(0, Math.min(value, max));
        int gridValue = Math.round((float) clampedValue / GRID_SIZE) * GRID_SIZE;
        int snappedValue = clampedValue;
        int closestDistance = SNAP_DISTANCE + 1;

        int[] snapTargets = {0, max, center, gridValue};
        for (int target : snapTargets) {
            int distance = Math.abs(clampedValue - target);
            if (distance <= SNAP_DISTANCE && distance < closestDistance) {
                snappedValue = target;
                closestDistance = distance;
            }
        }

        boolean snappedToCenter = snappedValue == center && Math.abs(clampedValue - center) <= SNAP_DISTANCE;
        boolean snappedToEdge = (snappedValue == 0 || snappedValue == max)
            && Math.abs(clampedValue - snappedValue) <= SNAP_DISTANCE;

        if (vertical) {
            this.showVerticalGuide = snappedToCenter || snappedToEdge;
        } else {
            this.showHorizontalGuide = snappedToCenter || snappedToEdge;
        }

        return Math.max(0, Math.min(snappedValue, max));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
            int boxWidth = this.font.width(text) + 12;
            int boxHeight = this.font.lineHeight + 9;

            if (mouseX >= ModConfig.hudX && mouseX <= ModConfig.hudX + boxWidth &&
                mouseY >= ModConfig.hudY && mouseY <= ModConfig.hudY + boxHeight) {

                this.isDragging = true;
                this.dragOffsetX = (int) mouseX - ModConfig.hudX;
                this.dragOffsetY = (int) mouseY - ModConfig.hudY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
            int boxWidth = this.font.width(text) + 12;
            int boxHeight = this.font.lineHeight + 9;
            ModConfig.rememberAnchor(this.width, this.height, boxWidth, boxHeight);
            this.isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        ModConfig.save();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}