package me.avie29.daycounter;

import com.mojang.brigadier.CommandDispatcher;
import me.avie29.daycounter.config.ConfigScreen;
import me.avie29.daycounter.config.ModConfig;
import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DayCounter.MOD_ID)
public class DayCounter {
    public static final String MOD_ID = "daycounter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public DayCounter() {
        ModConfig.load();
        NeoForge.EVENT_BUS.register(ClientEvents.class);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            KeyBindings.register(event);
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientEvents {
        private static int tickCounter;
        private static boolean openConfigScreenNextTick;

        @SubscribeEvent
        public static void registerClientCommands(RegisterClientCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            dispatcher.register(Commands.literal("dc")
                .then(Commands.literal("debug").executes(context -> {
                    ModConfig.debugEnabled = !ModConfig.debugEnabled;
                    ModConfig.save();
                    context.getSource().sendSuccess(() -> Component.translatable(ModConfig.debugEnabled
                        ? "message.daycounter.debug_enabled" : "message.daycounter.debug_disabled"), false);
                    return 1;
                }))
                .then(Commands.literal("config").executes(context -> {
                    openConfigScreenNextTick = true;
                    return 1;
                }))
                .then(Commands.literal("bgtoggle").executes(context -> {
                    ModConfig.backgroundVisible = !ModConfig.backgroundVisible;
                    ModConfig.save();
                    context.getSource().sendSuccess(() -> Component.translatable(ModConfig.backgroundVisible
                        ? "message.daycounter.background_enabled" : "message.daycounter.background_disabled"), false);
                    return 1;
                }))
                .then(Commands.literal("help").executes(context -> {
                    context.getSource().sendSuccess(() -> Component.translatable("message.daycounter.help_title"), false);
                    context.getSource().sendSuccess(() -> Component.translatable("message.daycounter.help_config"), false);
                    context.getSource().sendSuccess(() -> Component.translatable("message.daycounter.help_bgtoggle"), false);
                    context.getSource().sendSuccess(() -> Component.translatable("message.daycounter.help_debug"), false);
                    return 1;
                })));
        }

        @SubscribeEvent
        public static void renderGui(RenderGuiEvent.Post event) {
            HUD.render(event.getGuiGraphics());
        }

        @SubscribeEvent
        public static void clientTick(ClientTickEvent.Post event) {
            Minecraft client = Minecraft.getInstance();
            if (openConfigScreenNextTick) {
                openConfigScreenNextTick = false;
                client.setScreen(new ConfigScreen());
            }
            while (KeyBindings.toggleHudKey != null && KeyBindings.toggleHudKey.consumeClick()) {
                if (client.player != null) {
                    ModConfig.hudVisible = !ModConfig.hudVisible;
                    ModConfig.save();
                    client.player.displayClientMessage(Component.translatable(ModConfig.hudVisible
                        ? "message.daycounter.hud_enabled" : "message.daycounter.hud_disabled"), false);
                }
            }
            if (client.level == null || client.player == null || !ModConfig.debugEnabled) {
                return;
            }
            if (++tickCounter >= 100) {
                tickCounter = 0;
                logDebugInfo(client);
            }
        }

        private static void logDebugInfo(Minecraft client) {
            LOGGER.info("[Day Counter Debug] day={}, dayTime={}, gameTime={}, dimension={}, player=({}, {}, {}), hudVisible={}, backgroundVisible={}, customPosition={}, hudPosition=({}, {})",
                getDayCount.getCurrentDay(), client.level.getDayTime(), client.level.getGameTime(), client.level.dimension(),
                String.format("%.2f", client.player.getX()), String.format("%.2f", client.player.getY()), String.format("%.2f", client.player.getZ()),
                ModConfig.hudVisible, ModConfig.backgroundVisible, ModConfig.useCustomPosition, ModConfig.hudX, ModConfig.hudY);
        }
    }
}
