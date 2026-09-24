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
    public static float hudXRatio = 0.5f;
    public static float hudYRatio = 1.0f;
    public static boolean positionScaleAware = false;

    public static void updatePosition(int x, int y, int maxX, int maxY) {
        hudX = Math.max(0, Math.min(x, maxX));
        hudY = Math.max(0, Math.min(y, maxY));
        hudXRatio = ratio(hudX, maxX);
        hudYRatio = ratio(hudY, maxY);
        positionScaleAware = true;
    }

    public static int getHudX(int maxX) {
        return Math.round(clampRatio(hudXRatio) * Math.max(0, maxX));
    }

    public static int getHudY(int maxY) {
        return Math.round(clampRatio(hudYRatio) * Math.max(0, maxY));
    }

    public static void migrateLegacyPosition(int maxX, int maxY) {
        if (!positionScaleAware) {
            hudXRatio = ratio(hudX, maxX);
            hudYRatio = ratio(hudY, maxY);
            positionScaleAware = true;
            save();
        }
    }

    private static float ratio(int value, int max) {
        return max <= 0 ? 0.0f : clampRatio((float) value / max);
    }

    private static float clampRatio(float value) {
        return Math.max(0.0f, Math.min(value, 1.0f));
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
                hudXRatio = data.hudXRatio;
                hudYRatio = data.hudYRatio;
                positionScaleAware = data.positionScaleAware;
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
        data.hudXRatio = hudXRatio;
        data.hudYRatio = hudYRatio;
        data.positionScaleAware = positionScaleAware;

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
        float hudXRatio = 0.5f;
        float hudYRatio = 1.0f;
        boolean positionScaleAware = false;
    }
}