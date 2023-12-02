package io.github.Alathra.AlathranCommands.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PlaytimeChecker {

    public boolean checkPlaytime (Player player) {
        int playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int playtimeSeconds = playtime / 20;

        int cfgPlaytime = TPCfg.get().getInt("Settings.WildTP.Playtime");

        return playtimeSeconds > cfgPlaytime;
    }

    public static String playtimeString(OfflinePlayer offlinePlayer) {
        int playtime = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int playtimeSeconds = playtime / 20;

        int days = (int) TimeUnit.SECONDS.toDays(playtimeSeconds);
        long hours = TimeUnit.SECONDS.toHours(playtimeSeconds) - (days * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(playtimeSeconds) - (TimeUnit.SECONDS.toHours(playtimeSeconds) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(playtimeSeconds) - (TimeUnit.SECONDS.toMinutes(playtimeSeconds) * 60);

        if (days < 1) {
            if (hours < 1) {
                if (minutes < 1) {
                    return seconds + " seconds.";
                }
                return minutes + " minutes and " + seconds + " seconds";
            }
            return hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
        }
        return days + " days, " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
    }
}
