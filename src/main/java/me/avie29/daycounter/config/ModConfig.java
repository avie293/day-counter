package me.avie29.daycounter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("daycounter.json").toFile();

    public static boolean debugEnabled = false;
    public static boolean hudVisible = true;
    public static boolean backgroundVisible = true;
    
    public static boolean useCustomPosition = false;
    public static int hudX = 0;
    public static int hudY = 0;
    public static String hudHorizontalAnchor = "LEFT";
    public static String hudVerticalAnchor = "TOP";

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            @SuppressWarnings("null")
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                debugEnabled = data.debugEnabled;
                hudVisible = data.hudVisible;
                backgroundVisible = data.backgroundVisible;
                useCustomPosition = data.useCustomPosition;
                hudX = data.hudX;
                hudY = data.hudY;
                hudHorizontalAnchor = data.hudHorizontalAnchor;
                hudVerticalAnchor = data.hudVerticalAnchor;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.debugEnabled = debugEnabled;
        data.hudVisible = hudVisible;
        data.backgroundVisible = backgroundVisible;
        data.useCustomPosition = useCustomPosition;
        data.hudX = hudX;
        data.hudY = hudY;
        data.hudHorizontalAnchor = hudHorizontalAnchor;
        data.hudVerticalAnchor = hudVerticalAnchor;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        boolean debugEnabled = false;
        boolean hudVisible = true;
        boolean backgroundVisible = true;
        boolean useCustomPosition = false;
        int hudX = 0;
        int hudY = 0;
        String hudHorizontalAnchor = "LEFT";
        String hudVerticalAnchor = "TOP";
    }

    public static int resolveHudX(int screenWidth, int boxWidth) {
        return getHorizontalAnchorX(screenWidth, boxWidth) + hudX;
    }

    public static int resolveHudY(int screenHeight, int boxHeight) {
        return getVerticalAnchorY(screenHeight, boxHeight) + hudY;
    }

    public static void setHudPosition(int x, int y, int screenWidth, int screenHeight, int boxWidth, int boxHeight) {
        int maxX = screenWidth - boxWidth;
        int maxY = screenHeight - boxHeight;

        if (x == 0) {
            hudHorizontalAnchor = "LEFT";
        } else if (x == maxX) {
            hudHorizontalAnchor = "RIGHT";
        } else if (x == maxX / 2) {
            hudHorizontalAnchor = "CENTER";
        }

        if (y == 0) {
            hudVerticalAnchor = "TOP";
        } else if (y == maxY) {
            hudVerticalAnchor = "BOTTOM";
        } else if (y == maxY / 2) {
            hudVerticalAnchor = "MIDDLE";
        }

        hudX = x - getHorizontalAnchorX(screenWidth, boxWidth);
        hudY = y - getVerticalAnchorY(screenHeight, boxHeight);
    }

    private static int getHorizontalAnchorX(int screenWidth, int boxWidth) {
        return switch (hudHorizontalAnchor) {
            case "CENTER" -> (screenWidth - boxWidth) / 2;
            case "RIGHT" -> screenWidth - boxWidth;
            default -> 0;
        };
    }

    private static int getVerticalAnchorY(int screenHeight, int boxHeight) {
        return switch (hudVerticalAnchor) {
            case "MIDDLE" -> (screenHeight - boxHeight) / 2;
            case "BOTTOM" -> screenHeight - boxHeight;
            default -> 0;
        };
    }
}