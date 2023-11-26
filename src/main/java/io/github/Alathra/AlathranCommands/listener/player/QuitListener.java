package io.github.Alathra.AlathranCommands.listener.player;

import io.github.Alathra.AlathranCommands.data.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final PlayerManager playerManager = PlayerManager.getInstance();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        playerManager.removePlayer(p.getUniqueId());
    }
}