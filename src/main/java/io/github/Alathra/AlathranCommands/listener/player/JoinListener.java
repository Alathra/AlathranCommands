package io.github.Alathra.AlathranCommands.listener.player;

import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.data.model.PlayerData;
import io.github.Alathra.AlathranCommands.db.DatabaseQueries;
import io.github.Alathra.AlathranCommands.enums.CooldownType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;

public class JoinListener implements Listener {
    private final PlayerManager playerManager = PlayerManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        playerManager.setPlayer(p, new PlayerData());
        HashMap<CooldownType, Instant> tpaCooldown = DatabaseQueries.getCooldown(p);
        if (tpaCooldown == null) return;
        tpaCooldown.forEach((cooldownType, instant) -> {
            CooldownManager.getInstance().setCooldown(p, cooldownType, instant);
        });
    }
}
