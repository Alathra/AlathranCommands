package io.github.Alathra.AlathranCommands.data;

import io.github.Alathra.AlathranCommands.data.model.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton to store and access player data
 */
public class PlayerManager {
    private static PlayerManager instance;

    @NotNull
    public static PlayerManager getInstance() {
        if (instance == null) instance = new PlayerManager();
        return instance;
    }

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    @Nullable
    public PlayerData getPlayer(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    @Nullable
    public PlayerData getPlayer(Player p) {
        return getPlayer(p.getUniqueId());
    }

    public void setPlayer(UUID uuid, PlayerData playerData) {
        playerDataMap.put(uuid, playerData);
    }

    public void setPlayer(Player p, PlayerData playerData) {
        setPlayer(p.getUniqueId(), playerData);
    }

    public void removePlayer(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public void removePlayer(Player p) {
        removePlayer(p.getUniqueId());
    }
}
