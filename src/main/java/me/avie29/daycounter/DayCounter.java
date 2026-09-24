package me.avie29.daycounter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.avie29.daycounter.config.ConfigScreen;
import me.avie29.daycounter.config.ModConfig;
import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class DayCounter implements ClientModInitializer {

    public static final String MOD_ID = "day-counter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean debugMode = false;
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {

        ModConfig.load();
        KeyBindings.register();

            HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(DayCounter.MOD_ID, "day_hud"),
                HUD::render
            );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommands.literal("dc")
                    .then(ClientCommands.literal("debug")
                        .executes(context -> {
                            ModConfig.debugEnabled = !ModConfig.debugEnabled;
                            ModConfig.save();

                        if (ModConfig.debugEnabled) {
                            context.getSource().sendFeedback(
                                Component.translatable("message.daycounter.debug_enabled")
                            );
                        } else {
                            context.getSource().sendFeedback(
                                Component.translatable("message.daycounter.debug_disabled")
                            );
                        }

                            return 1;
                        })
                    )
                    .then(ClientCommands.literal("config")
                        .executes(context -> {
                            Minecraft.getInstance().execute(() -> {
                                Minecraft.getInstance().setScreen(new ConfigScreen());
                            });
                            return 1;
                        })
                    )
                    .then(ClientCommands.literal("bgtoggle")
                        .executes(context -> {
                            ModConfig.backgroundVisible = !ModConfig.backgroundVisible;
                            ModConfig.save();
                            context.getSource().sendFeedback(
                                Component.translatable(ModConfig.backgroundVisible
                                    ? "message.daycounter.background_enabled"
                                    : "message.daycounter.background_disabled")
                            );
                            return 1;
                        })
                    )
                    .then(ClientCommands.literal("help")
                        .executes(context -> {
                            context.getSource().sendFeedback(Component.translatable("message.daycounter.help_title"));
                            context.getSource().sendFeedback(Component.translatable("message.daycounter.help_config"));
                            context.getSource().sendFeedback(Component.translatable("message.daycounter.help_bgtoggle"));
                            context.getSource().sendFeedback(Component.translatable("message.daycounter.help_debug"));
                            return 1;
                        })
                    )
            );
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null) {
                return;
            }

            if (ModConfig.debugEnabled) {
               tickCounter++;

               if (tickCounter >= 100) {
                   tickCounter = 0;
                   logDebugInfo(client);
                }
            }
        });
    }

    private void logDebugInfo(Minecraft client) {
        var level = client.level;
        var player = client.player;
        if (level == null || player == null) {
            return;
        }

        LOGGER.info(
            "[Day Counter Debug] day={}, dayTime={}, gameTime={}, dimension={}, player=({}, {}, {}), hudVisible={}, backgroundVisible={}, customPosition={}, hudPosition=({}, {})",
            getDayCount.getCurrentDay(),
            level.getOverworldClockTime(),
            level.getGameTime(),
            level.dimension(),
            String.format("%.2f", player.getX()),
            String.format("%.2f", player.getY()),
            String.format("%.2f", player.getZ()),
            ModConfig.hudVisible,
            ModConfig.backgroundVisible,
            ModConfig.useCustomPosition,
            ModConfig.hudX,
            ModConfig.hudY
        );
    }
}