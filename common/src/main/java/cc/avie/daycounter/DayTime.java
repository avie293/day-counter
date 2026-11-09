package cc.avie.daycounter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

public final class DayTime {
    private DayTime() {
    }

    public static long getCurrentDay() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        if (player == null || player.level() == null) {
            return 0;
        }

        long time = player.level().getOverworldClockTime();
        long day = time / 24000L;

        return Math.max(0, day);
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
