package io.github.Alathra.AlathranCommands.utils;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaytimeChecker {

    public boolean checkPlaytime (Player player) {
        int playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int playtimeSeconds = playtime / 20;

        int cfgPlaytime = TPCfg.get().getInt("Settings.WildTP.Playtime");

        return playtimeSeconds > cfgPlaytime;
    }

}
