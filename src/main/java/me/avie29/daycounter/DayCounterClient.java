package me.avie29.daycounter;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.avie29.daycounter.config.DayCounterConfig;
import me.avie29.daycounter.hud.KeyBindings;
import me.avie29.tabbylib.api.TabbyLibApi;
import me.avie29.tabbylib.api.option.BooleanOption;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public final class DayCounterClient {
    public static final Logger LOGGER = LoggerFactory.getLogger("day-counter");
    private static int tickCounter = 0;

    private DayCounterClient() {
    }

    public static void init(String modId) {
        DayCounterConfig.init(modId);
    }

    public static <S> LiteralArgumentBuilder<S> command(BiConsumer<S, Component> feedback) {
        return LiteralArgumentBuilder.<S>literal("dc")
            .then(LiteralArgumentBuilder.<S>literal("debug")
                .executes(context -> {
                    boolean enabled = toggle(DayCounterConfig.DEBUG);
                    feedback.accept(context.getSource(), Component.translatable(enabled
                        ? "message.daycounter.debug_enabled"
                        : "message.daycounter.debug_disabled"));
                    return 1;
                })
            )
            .then(LiteralArgumentBuilder.<S>literal("config")
                .executes(context -> {
                    TabbyLibApi.openScreen(DayCounterConfig.get().getModId());
                    return 1;
                })
            )
            .then(LiteralArgumentBuilder.<S>literal("position")
                .executes(context -> {
                    TabbyLibApi.openHudEditor(DayCounterConfig.POSITION);
                    return 1;
                })
            )
            .then(LiteralArgumentBuilder.<S>literal("bgtoggle")
                .executes(context -> {
                    boolean enabled = toggle(DayCounterConfig.BACKGROUND_VISIBLE);
                    feedback.accept(context.getSource(), Component.translatable(enabled
                        ? "message.daycounter.background_enabled"
                        : "message.daycounter.background_disabled"));
                    return 1;
                })
            )
            .then(LiteralArgumentBuilder.<S>literal("help")
                .executes(context -> {
                    for (String key : new String[]{"help_title", "help_config", "help_position", "help_bgtoggle", "help_debug"}) {
                        feedback.accept(context.getSource(), Component.translatable("message.daycounter." + key));
                    }
                    return 1;
                })
            );
    }

    public static void tick(Minecraft client) {
        KeyBindings.handle(client);

        if (client.level == null || client.player == null) {
            return;
        }

        if (DayCounterConfig.DEBUG.get()) {
            tickCounter++;

            if (tickCounter >= 100) {
                tickCounter = 0;
                logDebugInfo(client);
            }
        }
    }

    private static boolean toggle(BooleanOption option) {
        option.set(!option.get());
        DayCounterConfig.get().save();
        return option.get();
    }

    private static void logDebugInfo(Minecraft client) {
        var level = client.level;
        var player = client.player;
        if (level == null || player == null) {
            return;
        }

        LOGGER.info(
            "[Day Counter Debug] day={}, dayTime={}, gameTime={}, dimension={}, player=({}, {}, {}), hudVisible={}, backgroundVisible={}, hudPosition={}",
            getDayCount.getCurrentDay(),
            level.getOverworldClockTime(),
            level.getGameTime(),
            level.dimension(),
            String.format("%.2f", player.getX()),
            String.format("%.2f", player.getY()),
            String.format("%.2f", player.getZ()),
            DayCounterConfig.HUD_VISIBLE.get(),
            DayCounterConfig.BACKGROUND_VISIBLE.get(),
            DayCounterConfig.POSITION.get()
        );
    }
}
