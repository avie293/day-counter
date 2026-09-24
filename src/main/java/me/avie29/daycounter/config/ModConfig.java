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
    public static int hudAnchorX = 0;
    public static int hudAnchorY = 0;
    public static int hudOffsetX = 0;
    public static int hudOffsetY = 0;
    public static boolean anchorPositionConfigured = false;

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
                if (data.hudAnchorX != null) {
                    hudAnchorX = data.hudAnchorX;
                    hudAnchorY = data.hudAnchorY;
                    hudOffsetX = data.hudOffsetX;
                    hudOffsetY = data.hudOffsetY;
                    anchorPositionConfigured = true;
                }
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
        data.hudAnchorX = hudAnchorX;
        data.hudAnchorY = hudAnchorY;
        data.hudOffsetX = hudOffsetX;
        data.hudOffsetY = hudOffsetY;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getHudX(int screenWidth, int boxWidth) {
        return getAnchoredCoordinate(screenWidth, boxWidth, hudAnchorX, hudOffsetX);
    }

    public static int getHudY(int screenHeight, int boxHeight) {
        return getAnchoredCoordinate(screenHeight, boxHeight, hudAnchorY, hudOffsetY);
    }

    public static void initializeAnchoredPosition(int screenWidth, int screenHeight, int boxWidth, int boxHeight) {
        if (anchorPositionConfigured) {
            hudX = getHudX(screenWidth, boxWidth);
            hudY = getHudY(screenHeight, boxHeight);
            return;
        }

        setAnchoredPosition(hudX, hudY, screenWidth, screenHeight, boxWidth, boxHeight);
        anchorPositionConfigured = true;
        save();
    }

    public static void setAnchoredPosition(int x, int y, int screenWidth, int screenHeight, int boxWidth, int boxHeight) {
        int maxX = Math.max(0, screenWidth - boxWidth);
        int maxY = Math.max(0, screenHeight - boxHeight);
        hudAnchorX = nearestAnchor(x, maxX);
        hudAnchorY = nearestAnchor(y, maxY);
        hudOffsetX = offsetFromAnchor(x, maxX, hudAnchorX);
        hudOffsetY = offsetFromAnchor(y, maxY, hudAnchorY);
        hudX = getHudX(screenWidth, boxWidth);
        hudY = getHudY(screenHeight, boxHeight);
    }

    private static int getAnchoredCoordinate(int screenSize, int boxSize, int anchor, int offset) {
        int max = Math.max(0, screenSize - boxSize);
        int coordinate = switch (anchor) {
            case 1 -> (max / 2) + offset;
            case 2 -> max - offset;
            default -> offset;
        };
        return Math.max(0, Math.min(coordinate, max));
    }

    private static int nearestAnchor(int coordinate, int max) {
        int center = max / 2;
        if (Math.abs(coordinate - center) <= 8) {
            return 1;
        }
        if (Math.abs(coordinate - max) <= 8) {
            return 2;
        }
        return 0;
    }

    private static int offsetFromAnchor(int coordinate, int max, int anchor) {
        return switch (anchor) {
            case 1 -> coordinate - (max / 2);
            case 2 -> max - coordinate;
            default -> coordinate;
        };
    }

    private static class ConfigData {
        boolean debugEnabled = false;
        boolean hudVisible = true;
        boolean backgroundVisible = true;
        boolean useCustomPosition = false;
        int hudX = 0;
        int hudY = 0;
        Integer hudAnchorX;
        Integer hudAnchorY;
        Integer hudOffsetX;
        Integer hudOffsetY;
    }
}