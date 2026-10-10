package me.avie29.daycounter.hud;

import me.avie29.daycounter.getDayCount;
import me.avie29.daycounter.config.DayCounterConfig;
import me.avie29.tabbylib.api.HudPosition;
import me.avie29.tabbylib.api.HudPreview;
import me.avie29.tabbylib.api.TabbyLibApi;
import me.avie29.tabbylib.api.option.Option;
import net.minecraft.ChatFormatting;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;

import java.util.Locale;

public class HUD {
    private static final int PADDING_X = 6;
    private static final int PADDING_Y = 4;
    private static final FontDescription COUNTER_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("day-counter", "counter"));
    private static final FontDescription ICON_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("day-counter", "icons"));
    private static final String BED = "";

    public static void render(GuiGraphicsExtractor guiGraphics) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        if (!DayCounterConfig.HUD_VISIBLE.get() || player == null || TabbyLibApi.isHudEditorOpen()) {
            return;
        }

        Layout layout = new Layout(client.font, false);
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        HudPosition position = DayCounterConfig.POSITION.get();
        layout.draw(guiGraphics, position.x(screenWidth, layout.width()), position.y(screenHeight, layout.height()));

        if (DayCounterConfig.BED_ICON.get() == DayCounterConfig.BedIcon.HOTBAR && getDayCount.canSleep() && !player.isSpectator()) {
            guiGraphics.text(client.font, bedIcon(), hotbarBedX(client, player, screenWidth), screenHeight - 15, 0xFFFFFFFF, false);
        }
    }

    private static int hotbarBedX(Minecraft client, LocalPlayer player, int screenWidth) {
        int x = screenWidth / 2 + 91 + 6;
        if (player.getMainArm() == HumanoidArm.LEFT && !player.getOffhandItem().isEmpty()) {
            x += 29;
        }
        if (player.getMainArm() == HumanoidArm.RIGHT && client.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            x += 24;
        }
        return x;
    }

    private static Component bedIcon() {
        return Component.literal(BED).withStyle(style -> style.withFont(ICON_FONT).withColor(ChatFormatting.WHITE).withoutShadow());
    }

    public static HudPreview preview() {
        return new HudPreview() {
            private Layout layout() {
                return new Layout(Minecraft.getInstance().font, true);
            }

            @Override
            public int width() {
                return this.layout().width();
            }

            @Override
            public int height() {
                return this.layout().height();
            }

            @Override
            public void render(GuiGraphicsExtractor graphics, int x, int y) {
                this.layout().draw(graphics, x, y);
            }
        };
    }

    public static Component text(boolean pending) {
        long day = getDayCount.getCurrentDay(pending);
        DayCounterConfig.TextFormat format = value(DayCounterConfig.TEXT_FORMAT, pending);
        Component text = switch (format) {
            case DAY -> Component.translatable("hud.day", day);
            case NUMBER -> Component.literal(Long.toString(day));
            case DAY_AND_TIME -> Component.translatable("hud.day_time", day, time(pending));
            case CUSTOM -> Component.literal(value(DayCounterConfig.CUSTOM_TEXT, pending)
                .replace("{day}", Long.toString(day))
                .replace("{time}", time(pending)));
        };

        MutableComponent result;
        if (value(DayCounterConfig.FONT, pending) == DayCounterConfig.FontStyle.NEW) {
            String string = format == DayCounterConfig.TextFormat.CUSTOM ? text.getString() : text.getString().toUpperCase(Locale.ROOT);
            result = Component.literal(string).withStyle(style -> style.withFont(COUNTER_FONT));
        } else {
            result = text.copy();
        }

        if (value(DayCounterConfig.BED_ICON, pending) == DayCounterConfig.BedIcon.COUNTER && (pending || getDayCount.canSleep())) {
            result = Component.empty().append(result).append(" ").append(bedIcon());
        }
        return result;
    }

    private static String time(boolean pending) {
        Minecraft client = Minecraft.getInstance();
        long dayTime = client.level == null ? 6000 : Math.floorMod(getDayCount.getClockTime(client.level, pending), 24000L);
        int hours = (int) ((dayTime / 1000 + 6) % 24);
        int minutes = (int) (dayTime % 1000 * 60 / 1000);
        if (value(DayCounterConfig.TWELVE_HOUR, pending)) {
            String suffix = hours < 12 ? "AM" : "PM";
            int twelve = hours % 12 == 0 ? 12 : hours % 12;
            return String.format(Locale.ROOT, "%d:%02d %s", twelve, minutes, suffix);
        }
        return String.format(Locale.ROOT, "%02d:%02d", hours, minutes);
    }

    private static <T> T value(Option<T> option, boolean pending) {
        return pending ? option.getPending() : option.get();
    }

    private record Layout(Font font, boolean pending, Component text, float scale) {
        Layout(Font font, boolean pending) {
            this(font, pending, HUD.text(pending), value(DayCounterConfig.SCALE, pending).floatValue());
        }

        private int unscaledWidth() {
            return this.font.width(this.text) + PADDING_X * 2;
        }

        private int unscaledHeight() {
            return this.font.lineHeight + PADDING_Y * 2 + 1;
        }

        int width() {
            return Math.round(this.unscaledWidth() * this.scale);
        }

        int height() {
            return Math.round(this.unscaledHeight() * this.scale);
        }

        void draw(GuiGraphicsExtractor graphics, int x, int y) {
            int w = this.unscaledWidth();
            int h = this.unscaledHeight();

            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y);
            graphics.pose().scale(this.scale, this.scale);

            if (value(DayCounterConfig.BACKGROUND_VISIBLE, this.pending)) {
                int color = value(DayCounterConfig.BACKGROUND_COLOR, this.pending);
                graphics.fill(1, 0, w - 1, 1, color);
                graphics.fill(0, 1, w, h - 1, color);
                graphics.fill(1, h - 1, w - 1, h, color);
            }
            if (value(DayCounterConfig.BORDER_VISIBLE, this.pending)) {
                int color = value(DayCounterConfig.BORDER_COLOR, this.pending);
                graphics.fill(1, 0, w - 1, 1, color);
                graphics.fill(1, h - 1, w - 1, h, color);
                graphics.fill(0, 1, 1, h - 1, color);
                graphics.fill(w - 1, 1, w, h - 1, color);
            }

            graphics.text(this.font, this.text, PADDING_X, PADDING_Y + 1,
                value(DayCounterConfig.TEXT_COLOR, this.pending), value(DayCounterConfig.TEXT_SHADOW, this.pending));
            graphics.pose().popMatrix();
        }
    }
}
