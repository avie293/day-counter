package me.avie29.daycounter;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import me.avie29.daycounter.config.ConfigScreen;
import me.avie29.daycounter.config.ModConfig;
import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;

@Mod(DayCounter.MOD_ID)
public class DayCounter {

    public static final String MOD_ID = "day_counter";
    public static final Logger LOGGER = LogUtils.getLogger();
    private int tickCounter = 0;

    public DayCounter(IEventBus modEventBus, ModContainer modContainer) {
        ModConfig.load();
        modContainer.registerExtensionPoint(
            IConfigScreenFactory.class,
            (container, modListScreen) -> new ConfigScreen(modListScreen)
        );
        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::registerGuiLayers);
        NeoForge.EVENT_BUS.register(this);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }

    private void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            Identifier.fromNamespaceAndPath(MOD_ID, "day_hud"),
            HUD::render
        );
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void registerClientCommands(RegisterClientCommandsEvent event) {
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
                                Minecraft.getInstance().setScreenAndShow(new ConfigScreen());
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

    @net.neoforged.bus.api.SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        KeyBindings.handleClientTick(client);
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