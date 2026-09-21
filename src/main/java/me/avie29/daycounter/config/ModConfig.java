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

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
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
            }
        } catch (IOException e) {
            System.err.println("Unable to load Day Counter config: " + e.getMessage());
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

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Unable to save Day Counter config: " + e.getMessage());
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
    }
}