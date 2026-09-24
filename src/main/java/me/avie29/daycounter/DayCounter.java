package me.avie29.daycounter;

import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.avie29.daycounter.config.ConfigScreen;
import me.avie29.daycounter.config.ModConfig;
import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.minecraft.resources.Identifier;

@Mod(value = DayCounter.MOD_ID, dist = Dist.CLIENT)
public class DayCounter {

    public static final String MOD_ID = "day_counter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean debugMode = false;
    private int tickCounter = 0;

    public DayCounter(IEventBus modEventBus) {
        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::registerHud);
        NeoForge.EVENT_BUS.addListener(this::registerClientCommands);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);

        ModConfig.load();
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }

    private void registerHud(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            Identifier.fromNamespaceAndPath(MOD_ID, "day_hud"),
            HUD::render
        );
    }

    @SubscribeEvent
    private void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("dc")
                    .then(Commands.literal("debug")
                        .executes(context -> {
                            ModConfig.debugEnabled = !ModConfig.debugEnabled;
                            ModConfig.save();

                        if (ModConfig.debugEnabled) {
                            context.getSource().sendSystemMessage(
                                Component.translatable("message.daycounter.debug_enabled")
                            );
                        } else {
                            context.getSource().sendSystemMessage(
                                Component.translatable("message.daycounter.debug_disabled")
                            );
                        }

                            return 1;
                        })
                    )
                    .then(Commands.literal("config")
                        .executes(context -> {
                            Minecraft.getInstance().execute(() -> {
                                Minecraft.getInstance().setScreen(new ConfigScreen());
                            });
                            return 1;
                        })
                    )
                    .then(Commands.literal("bgtoggle")
                        .executes(context -> {
                            ModConfig.backgroundVisible = !ModConfig.backgroundVisible;
                            ModConfig.save();
                            context.getSource().sendSystemMessage(
                                Component.translatable(ModConfig.backgroundVisible
                                    ? "message.daycounter.background_enabled"
                                    : "message.daycounter.background_disabled")
                            );
                            return 1;
                        })
                    )
                    .then(Commands.literal("help")
                        .executes(context -> {
                            context.getSource().sendSystemMessage(Component.translatable("message.daycounter.help_title"));
                            context.getSource().sendSystemMessage(Component.translatable("message.daycounter.help_config"));
                            context.getSource().sendSystemMessage(Component.translatable("message.daycounter.help_bgtoggle"));
                            context.getSource().sendSystemMessage(Component.translatable("message.daycounter.help_debug"));
                            return 1;
                        })
                    )
        );
    }

    @SubscribeEvent
    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
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