package me.avie29.daycounter;

import me.avie29.daycounter.config.DayCounterConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

public class getDayCount {

    public static long getCurrentDay() {
        return getCurrentDay(false);
    }

    public static long getCurrentDay(boolean pending) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        if (player == null || player.level() == null) {
            return 0;
        }

        long time = getClockTime(player.level(), pending);
        long day = time / 24000L;

        return Math.max(0, day);
    }

    public static long getClockTime(Level level, boolean pending) {
        DayCounterConfig.DimensionDay mode = pending ? DayCounterConfig.DIMENSION_DAY.getPending() : DayCounterConfig.DIMENSION_DAY.get();
        if (mode == DayCounterConfig.DimensionDay.DIMENSION && level.dimensionType().defaultClock().isPresent()) {
            return level.getDefaultClockTime();
        }
        return level.getOverworldClockTime();
    }

    public static boolean canSleep() {
        Minecraft client = Minecraft.getInstance();
        Level level = client.level;

        if (level == null || level.dimension() != Level.OVERWORLD) {
            return false;
        }

        long dayTime = Math.floorMod(level.getOverworldClockTime(), 24000L);
        return (dayTime >= 12542 && dayTime <= 23460) || level.isThundering();
    }
}
