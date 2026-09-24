package me.avie29.daycounter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.avie29.daycounter.config.ConfigScreen;
import me.avie29.daycounter.config.ModConfig;
import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DayCounter implements ClientModInitializer {

    public static final String MOD_ID = "day-counter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean debugMode = false;
    private int tickCounter = 0;
    private boolean openConfigScreenNextTick = false;

    @Override
    public void onInitializeClient() {

        ModConfig.load();
        KeyBindings.register();

        HudRenderCallback.EVENT.register(HUD::render);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("dc")
                    .then(ClientCommandManager.literal("debug")
                        .executes(context -> {
                            ModConfig.debugEnabled = !ModConfig.debugEnabled;
                            ModConfig.save();

                        if (ModConfig.debugEnabled) {
                            context.getSource().sendFeedback(
                                Text.translatable("message.daycounter.debug_enabled")
                            );
                        } else {
                            context.getSource().sendFeedback(
                                Text.translatable("message.daycounter.debug_disabled")
                            );
                        }

                            return 1;
                        })
                    )
                    .then(ClientCommandManager.literal("config")
                        .executes(context -> {
                            openConfigScreenNextTick = true;
                            return 1;
                        })
                    )
                    .then(ClientCommandManager.literal("bgtoggle")
                        .executes(context -> {
                            ModConfig.backgroundVisible = !ModConfig.backgroundVisible;
                            ModConfig.save();
                            context.getSource().sendFeedback(
                                Text.translatable(ModConfig.backgroundVisible
                                    ? "message.daycounter.background_enabled"
                                    : "message.daycounter.background_disabled")
                            );
                            return 1;
                        })
                    )
                    .then(ClientCommandManager.literal("help")
                        .executes(context -> {
                            context.getSource().sendFeedback(Text.translatable("message.daycounter.help_title"));
                            context.getSource().sendFeedback(Text.translatable("message.daycounter.help_config"));
                            context.getSource().sendFeedback(Text.translatable("message.daycounter.help_bgtoggle"));
                            context.getSource().sendFeedback(Text.translatable("message.daycounter.help_debug"));
                            return 1;
                        })
                    )
            );
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigScreenNextTick) {
                openConfigScreenNextTick = false;
                client.setScreen(new ConfigScreen());
            }

            if (client.world == null || client.player == null) {
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

    private void logDebugInfo(MinecraftClient client) {
        var level = client.world;
        var player = client.player;
        if (level == null || player == null) {
            return;
        }

        LOGGER.info(
            "[Day Counter Debug] day={}, dayTime={}, gameTime={}, dimension={}, player=({}, {}, {}), hudVisible={}, backgroundVisible={}, customPosition={}, hudPosition=({}, {})",
            getDayCount.getCurrentDay(),
            level.getTimeOfDay(),
            level.getTime(),
            level.getRegistryKey(),
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