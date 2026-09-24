package me.avie29.daycounter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class getDayCount {

    public static long getCurrentDay() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || player.getWorld() == null) {
            return 0;
        }

        long time = player.getWorld().getTimeOfDay();
        long day = time / 24000L;

        return Math.max(0, day);
    }
}