package me.avie29.daycounter.config;

import me.avie29.daycounter.getDayCount;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ConfigScreen extends Screen {

    private static final int SNAP_DISTANCE = 8;
    private static final int GRID_SIZE = 10;

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private boolean showVerticalGuide = false;
    private boolean showHorizontalGuide = false;
    private final Screen parent;

    public ConfigScreen() {
        this(null);
    }

    public ConfigScreen(Screen parent) {
        super(Component.translatable("screen.daycounter.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (ModConfig.useCustomPosition
            && ModConfig.hudScreenWidth > 0
            && ModConfig.hudScreenHeight > 0
            && (ModConfig.hudScreenWidth != this.width || ModConfig.hudScreenHeight != this.height)) {
            ModConfig.hudX = Math.round((float) ModConfig.hudX * this.width / ModConfig.hudScreenWidth);
            ModConfig.hudY = Math.round((float) ModConfig.hudY * this.height / ModConfig.hudScreenHeight);
        }

        ModConfig.hudScreenWidth = this.width;
        ModConfig.hudScreenHeight = this.height;

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
    }

    private void resetPosition() {
        setDefaultPosition();
        ModConfig.useCustomPosition = true;
        ModConfig.save();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.extractBackground(guiGraphics, mouseX, mouseY, partialTick);

        Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
        int textWidth = this.font.width(text);
        int textHeight = this.font.lineHeight;
        int paddingX = 6;
        int paddingY = 4;
        int boxWidth = textWidth + (paddingX * 2);
        int boxHeight = textHeight + (paddingY * 2) + 1;

        if (this.isDragging) {
            updateHudPosition(mouseX, mouseY, boxWidth, boxHeight);
        } else {
            this.showVerticalGuide = false;
            this.showHorizontalGuide = false;
        }

        int x = ModConfig.hudX;
        int y = ModConfig.hudY;

        if (this.isDragging) {
            if (this.showVerticalGuide) {
                int guideX = x + (boxWidth / 2);
                guiGraphics.fill(guideX, 0, guideX + 1, this.height, 0x8033CCFF);
            }

            if (this.showHorizontalGuide) {
                int guideY = y + (boxHeight / 2);
                guiGraphics.fill(0, guideY, this.width, guideY + 1, 0x8033CCFF);
            }
        }

        if (ModConfig.backgroundVisible) {
            guiGraphics.fill(x + 1, y, x + boxWidth - 1, y + 1, 0x90000000);
            guiGraphics.fill(x, y + 1, x + boxWidth, y + boxHeight - 1, 0x90000000);
            guiGraphics.fill(x + 1, y + boxHeight - 1, x + boxWidth - 1, y + boxHeight, 0x90000000);
        }

        guiGraphics.fill(x + 1, y, x + boxWidth - 1, y + 1, 0xFFFFFF00);
        guiGraphics.fill(x + 1, y + boxHeight - 1, x + boxWidth - 1, y + boxHeight, 0xFFFFFF00);
        guiGraphics.fill(x, y + 1, x + 1, y + boxHeight - 1, 0xFFFFFF00);
        guiGraphics.fill(x + boxWidth - 1, y + 1, x + boxWidth, y + boxHeight - 1, 0xFFFFFF00);

        guiGraphics.text(this.font, text, x + paddingX, y + paddingY + 1, 0xFFFFFFFF, true);

        Component hint1 = Component.translatable("screen.daycounter.config.hint_snap");
        Component hint2 = Component.translatable("screen.daycounter.config.hint_controls");
        guiGraphics.text(this.font, hint1, (this.width / 2) - (this.font.width(hint1) / 2), 20, 0xFFFFFFFF, true);
        guiGraphics.text(this.font, hint2, (this.width / 2) - (this.font.width(hint2) / 2), 32, 0xAAAAAAFF, true);

        if (Minecraft.getInstance().level == null) {
            Component preview = Component.translatable("screen.daycounter.config.preview");
            guiGraphics.text(this.font, preview, (this.width / 2) - (this.font.width(preview) / 2), 48, 0xFFFF55FF, true);
        }

        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
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

    private void updateHudPosition(int mouseX, int mouseY, int boxWidth, int boxHeight) {
        int rawX = mouseX - this.dragOffsetX;
        int rawY = mouseY - this.dragOffsetY;
        int maxX = this.width - boxWidth;
        int maxY = this.height - boxHeight;

        ModConfig.hudX = snapCoordinate(rawX, maxX, (this.width - boxWidth) / 2, true);
        ModConfig.hudY = snapCoordinate(rawY, maxY, (this.height - boxHeight) / 2, false);

        ModConfig.hudX = Math.max(0, Math.min(ModConfig.hudX, this.width - boxWidth));
        ModConfig.hudY = Math.max(0, Math.min(ModConfig.hudY, this.height - boxHeight));
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1) {
            Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
            int boxWidth = this.font.width(text) + 12;
            int boxHeight = this.font.lineHeight + 9;

            if (event.x() >= ModConfig.hudX && event.x() <= ModConfig.hudX + boxWidth &&
                event.y() >= ModConfig.hudY && event.y() <= ModConfig.hudY + boxHeight) {

                this.isDragging = true;
                this.setDragging(true);
                this.dragOffsetX = (int) event.x() - ModConfig.hudX;
                this.dragOffsetY = (int) event.y() - ModConfig.hudY;
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        if (this.isDragging) {
            Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
            int boxWidth = this.font.width(text) + 12;
            int boxHeight = this.font.lineHeight + 9;
            updateHudPosition((int) event.x(), (int) event.y(), boxWidth, boxHeight);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (this.isDragging) {
            Component text = Component.translatable("hud.day", getDayCount.getCurrentDay());
            int boxWidth = this.font.width(text) + 12;
            int boxHeight = this.font.lineHeight + 9;
            updateHudPosition((int) mouseX, (int) mouseY, boxWidth, boxHeight);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (event.button() == 1) {
            this.isDragging = false;
            this.setDragging(false);
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        ModConfig.save();
        Minecraft.getInstance().setScreenAndShow(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }
}