package me.avie29.daycounter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class getDayCount {

    public static long getCurrentDay() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        if (player == null || player.level() == null) {
            return 0;
        }

        long time = player.level().getDayTime();
        long day = time / 24000L;

        return Math.max(0, day);
    }
}