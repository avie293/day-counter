package me.avie29.daycounter;

import com.mojang.logging.LogUtils;
import me.avie29.daycounter.config.ConfigScreen;
import me.avie29.daycounter.config.ModConfig;
import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DayCounter.MOD_ID)
public class DayCounter {

    public static final String MOD_ID = "day_counter";
    public static final Logger LOGGER = LogUtils.getLogger();
    private int tickCounter = 0;

    public DayCounter() {
        ModConfig.load();
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen))
        );
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeyMappings);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerGuiOverlays);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }

    private void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CHAT_PANEL.id(), "day_hud", HUD::render);
    }

    @SubscribeEvent
    public void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("dc")
                .then(Commands.literal("debug").executes(context -> {
                    ModConfig.debugEnabled = !ModConfig.debugEnabled;
                    ModConfig.save();
                    context.getSource().sendSystemMessage(Component.translatable(
                        ModConfig.debugEnabled ? "day_counter.message.debug_enabled" : "day_counter.message.debug_disabled"));
                    return 1;
                }))
                .then(Commands.literal("config").executes(context -> {
                    Minecraft.getInstance().setScreen(new ConfigScreen());
                    return 1;
                }))
                .then(Commands.literal("bgtoggle").executes(context -> {
                    ModConfig.backgroundVisible = !ModConfig.backgroundVisible;
                    ModConfig.save();
                    context.getSource().sendSystemMessage(Component.translatable(
                        ModConfig.backgroundVisible ? "day_counter.message.background_enabled" : "day_counter.message.background_disabled"));
                    return 1;
                }))
                .then(Commands.literal("help").executes(context -> {
                    context.getSource().sendSystemMessage(Component.translatable("day_counter.message.help_title"));
                    context.getSource().sendSystemMessage(Component.translatable("day_counter.message.help_config"));
                    context.getSource().sendSystemMessage(Component.translatable("day_counter.message.help_bgtoggle"));
                    context.getSource().sendSystemMessage(Component.translatable("day_counter.message.help_debug"));
                    return 1;
                }))
        );
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        KeyBindings.handleClientTick(client);
        if (client.level == null || client.player == null || !ModConfig.debugEnabled) {
            return;
        }

        tickCounter++;
        if (tickCounter >= 100) {
            tickCounter = 0;
            logDebugInfo(client);
        }
    }

    private void logDebugInfo(Minecraft client) {
        if (client.level == null || client.player == null) {
            return;
        }

        LOGGER.info(
            "[Day Counter Debug] day={}, dayTime={}, gameTime={}, dimension={}, player=({}, {}, {}), hudVisible={}, backgroundVisible={}, customPosition={}, hudPosition=({}, {})",
            getDayCount.getCurrentDay(),
            client.level.getDayTime(),
            client.level.getGameTime(),
            client.level.dimension(),
            String.format("%.2f", client.player.getX()),
            String.format("%.2f", client.player.getY()),
            String.format("%.2f", client.player.getZ()),
            ModConfig.hudVisible,
            ModConfig.backgroundVisible,
            ModConfig.useCustomPosition,
            ModConfig.hudX,
            ModConfig.hudY
        );
    }
}
