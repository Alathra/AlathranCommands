package io.github.Alathra.AlathranCommands.listener.player;

import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.db.DatabaseQueries;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        DatabaseQueries.saveCooldown(p);
        PlayerManager.getInstance().removePlayer(p);
        CooldownManager.getInstance().clearCooldowns(p);
    }
}