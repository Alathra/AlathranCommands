package io.github.Alathra.AlathranCommands.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.Alathra.AlathranCommands.enums.CooldownType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Singleton to store and access player cooldowns
 */
public class CooldownManager {
    private static CooldownManager instance;

    @NotNull
    public static CooldownManager getInstance() {
        if (instance == null) instance = new CooldownManager();
        return instance;
    }

    private final Table<UUID, CooldownType, Instant> cooldowns = HashBasedTable.create();

    // Set cooldown
    @Nullable
    public Instant setCooldown(UUID uuid, CooldownType type, Instant instant) {
        return cooldowns.put(uuid, type, instant);
    }

    @Nullable
    public Instant setCooldown(UUID uuid, CooldownType type, Duration duration) {
        return setCooldown(uuid, type, Instant.now().plus(duration));
    }

    @Nullable
    public Instant setCooldown(Player p, CooldownType type, Instant instant) {
        return setCooldown(p.getUniqueId(), type, instant);
    }

    @Nullable
    public Instant setCooldown(Player p, CooldownType type, Duration duration) {
        return setCooldown(p.getUniqueId(), type, Instant.now().plus(duration));
    }

    @Nullable
    public Instant setCooldown(Player p, CooldownType type, int seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        return setCooldown(p.getUniqueId(), type, Instant.now().plus(duration));
    }

    // Check if cooldown has expired
    public boolean hasCooldown(UUID uuid, CooldownType type) {
        Instant cooldown = cooldowns.get(uuid, type);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    public boolean hasCooldown(Player p, CooldownType type) {
        return hasCooldown(p.getUniqueId(), type);
    }

    // Remove cooldown
    @Nullable
    public Instant removeCooldown(UUID uuid, CooldownType type) {
        return cooldowns.remove(uuid, type);
    }

    @Nullable
    public Instant removeCooldown(Player p, CooldownType type) {
        return removeCooldown(p.getUniqueId(), type);
    }

    // Clear all cooldowns
    public void clearCooldowns(UUID uuid) {
        cooldowns.row(uuid).clear();
    }

    public void clearCooldowns (Player p) {
        clearCooldowns(p.getUniqueId());
    }

    // Get remaining cooldown time
    @Nullable
    public Instant getCooldown(UUID uuid, CooldownType type) {
        return cooldowns.get(uuid, type);
    }

    @Nullable
    public Instant getCooldown(Player p, CooldownType type) {
        return getCooldown(p.getUniqueId(), type);
    }

    // Get remaining cooldown time
    public Duration getRemainingCooldown(UUID uuid, CooldownType type) {
        Instant cooldown = cooldowns.get(uuid, type);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            removeCooldown(uuid, type);
            return Duration.ZERO;
        }
    }

    public Duration getRemainingCooldown(Player p, CooldownType type) {
        return getRemainingCooldown(p.getUniqueId(), type);
    }

    public String getRemainingCooldownString(Player p, CooldownType type) {
        long cooldownTime = getRemainingCooldown(p, type).getSeconds();
        long hours = TimeUnit.SECONDS.toHours(cooldownTime);
        long minutes = TimeUnit.SECONDS.toMinutes(cooldownTime) - (TimeUnit.SECONDS.toHours(cooldownTime) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(cooldownTime) - (TimeUnit.SECONDS.toMinutes(cooldownTime) * 60);

        if (hours < 1) {
            if (minutes < 1) {
                return seconds + " seconds.";
            }
            return minutes + " minutes and " + seconds + " seconds.";
        }
        return hours + " hours, " + minutes + " minutes and " + seconds + " seconds ";
    }

    public void reset() {
        instance = new CooldownManager();
    }
}
