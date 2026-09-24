package me.avie29.daycounter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FMLPaths.CONFIGDIR.get().resolve("daycounter.json").toFile();

    public static boolean debugEnabled = false;
    public static boolean hudVisible = true;
    public static boolean backgroundVisible = true;
    
    public static boolean useCustomPosition = false;
    public static int hudX = 0;
    public static int hudY = 0;
    public static int hudScreenWidth = 0;
    public static int hudScreenHeight = 0;
    public static int hudAnchorX = 0;
    public static int hudAnchorY = 0;
    public static int hudOffsetX = 0;
    public static int hudOffsetY = 0;
    public static boolean hudAnchorInitialized = false;

    public static void updatePositionForScreen(int screenWidth, int screenHeight, int boxWidth, int boxHeight) {
        if (!useCustomPosition) {
            hudScreenWidth = screenWidth;
            hudScreenHeight = screenHeight;
            return;
        }

        if (!hudAnchorInitialized) {
            if (hudScreenWidth > 0 && hudScreenHeight > 0 &&
                (hudScreenWidth != screenWidth || hudScreenHeight != screenHeight)) {
                hudX = Math.round((float) hudX * screenWidth / hudScreenWidth);
                hudY = Math.round((float) hudY * screenHeight / hudScreenHeight);
            }
            rememberAnchor(screenWidth, screenHeight, boxWidth, boxHeight);
        } else if (hudScreenWidth != screenWidth || hudScreenHeight != screenHeight) {
            hudX = getAnchorCoordinate(hudAnchorX, hudOffsetX, screenWidth, boxWidth);
            hudY = getAnchorCoordinate(hudAnchorY, hudOffsetY, screenHeight, boxHeight);
        }

        hudScreenWidth = screenWidth;
        hudScreenHeight = screenHeight;
    }

    public static void rememberAnchor(int screenWidth, int screenHeight, int boxWidth, int boxHeight) {
        int maxX = Math.max(0, screenWidth - boxWidth);
        int maxY = Math.max(0, screenHeight - boxHeight);
        hudX = Math.max(0, Math.min(hudX, maxX));
        hudY = Math.max(0, Math.min(hudY, maxY));

        hudAnchorX = getClosestAnchor(hudX, 0, maxX / 2, maxX);
        hudAnchorY = getClosestAnchor(hudY, 0, maxY / 2, maxY);
        hudOffsetX = hudX - getAnchorCoordinate(hudAnchorX, 0, screenWidth, boxWidth);
        hudOffsetY = hudY - getAnchorCoordinate(hudAnchorY, 0, screenHeight, boxHeight);
        hudAnchorInitialized = true;
    }

    private static int getClosestAnchor(int value, int start, int center, int end) {
        int distanceToStart = Math.abs(value - start);
        int distanceToCenter = Math.abs(value - center);
        int distanceToEnd = Math.abs(value - end);

        if (distanceToStart <= distanceToCenter && distanceToStart <= distanceToEnd) {
            return 0;
        }
        if (distanceToCenter <= distanceToEnd) {
            return 1;
        }
        return 2;
    }

    private static int getAnchorCoordinate(int anchor, int offset, int screenSize, int boxSize) {
        int max = Math.max(0, screenSize - boxSize);
        int anchorCoordinate = anchor == 0 ? 0 : anchor == 1 ? max / 2 : max;
        return Math.max(0, Math.min(anchorCoordinate + offset, max));
    }

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
                hudScreenWidth = data.hudScreenWidth;
                hudScreenHeight = data.hudScreenHeight;
                hudAnchorX = data.hudAnchorX;
                hudAnchorY = data.hudAnchorY;
                hudOffsetX = data.hudOffsetX;
                hudOffsetY = data.hudOffsetY;
                hudAnchorInitialized = data.hudAnchorInitialized;
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
        data.hudScreenWidth = hudScreenWidth;
        data.hudScreenHeight = hudScreenHeight;
        data.hudAnchorX = hudAnchorX;
        data.hudAnchorY = hudAnchorY;
        data.hudOffsetX = hudOffsetX;
        data.hudOffsetY = hudOffsetY;
        data.hudAnchorInitialized = hudAnchorInitialized;

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
        int hudScreenWidth = 0;
        int hudScreenHeight = 0;
        int hudAnchorX = 0;
        int hudAnchorY = 0;
        int hudOffsetX = 0;
        int hudOffsetY = 0;
        boolean hudAnchorInitialized = false;
    }
}