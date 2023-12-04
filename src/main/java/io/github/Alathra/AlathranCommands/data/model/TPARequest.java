package io.github.Alathra.AlathranCommands.data.model;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.enums.CooldownType;
import io.github.Alathra.AlathranCommands.enums.TeleportType;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * The type Tpa request.
 */
public class TPARequest {
    private final long timeout;
    private final Player origin; // Always the sender of the request
    private final Player target; // Always the responder of the request
    private final TeleportType type;
    private boolean processed = false; // Has this request been processed (true when a command uses the request)
    private long time;
    private int price = 0;
    private int tpaCooldown = 0;


    /**
     * Instantiates a new Tpa request.
     *
     * @param origin the origin
     * @param target the target
     * @param type the type
     */
    public TPARequest(Player origin, Player target, TeleportType type) {
        this.origin = origin;
        this.target = target;
        this.type = type;
        this.time = System.currentTimeMillis();

        this.timeout = TimeUnit.SECONDS.toMillis(TPCfg.get().getInt("Settings.TPA.Timeout"));

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.price = TPCfg.get().getInt("Settings.TPA.Price");
        }
        this.tpaCooldown = TPCfg.get().getInt("Settings.TPA.Cooldown");
    }

    /**
     * Gets origin. Always the sender of the request.
     *
     * @return the origin
     */
    public Player getOrigin() {
        return origin;
    }

    /**
     * Gets target. Always the responder of the request
     * @return the target
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public TeleportType getType() {
        return type;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Check if this request has expired
     *
     * @return boolean boolean
     */
    public boolean isExpired() {
        return !( timeout < 1 || ( System.currentTimeMillis() - getTime() ) <= timeout );
    }

    /**
     * Can afford boolean.
     *
     * @return the boolean
     */
    public boolean canAfford() {
        if (AlathranCommands.getVaultHook().isVaultLoaded()) {
            Economy economy = AlathranCommands.getVaultHook().getVault();
            switch (this.getType()) {
                case TPA -> {
                    return economy.getBalance(Bukkit.getOfflinePlayer(this.getOrigin().getUniqueId())) > price;
                }
                case TPAHERE -> {
                    return economy.getBalance(Bukkit.getOfflinePlayer(this.getTarget().getUniqueId())) > price;
                }
            }
        }
        return true;
    }

    /**
     * Make origin pay price.
     */
    public void payPrice() {
        if (AlathranCommands.getVaultHook().isVaultLoaded()) {
            Economy economy = AlathranCommands.getVaultHook().getVault();
            switch (this.getType()) {
                case TPA -> economy.withdrawPlayer(this.getOrigin(), price);
                case TPAHERE -> economy.withdrawPlayer(this.getTarget(), price);
            }
        }
    }

    /**
     * Has cooldown boolean.
     *
     * @return the boolean
     */
    public boolean hasCooldown() {
        CooldownManager cooldownManager = CooldownManager.getInstance();
        switch (this.getType()) {
            case TPA -> {
                return cooldownManager.hasCooldown(this.getOrigin(), CooldownType.TELEPORT_PLAYER);
            }
            case TPAHERE -> {
                return cooldownManager.hasCooldown(this.getTarget(), CooldownType.TELEPORT_PLAYER);
            }
            default -> {
                return true;
            }
        }
    }

    public void setCooldown() {
        CooldownManager cooldownManager = CooldownManager.getInstance();
        switch (this.getType()) {
            case TPA -> cooldownManager.setCooldown(this.getOrigin(), CooldownType.TELEPORT_PLAYER, tpaCooldown);
            case TPAHERE -> cooldownManager.setCooldown(this.getTarget(), CooldownType.TELEPORT_PLAYER, tpaCooldown);
        }
    }

    /**
     * Return if this request has been processed.
     *
     * @return the boolean
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Sets if this request has been processed.
     *
     * @param processed the processed
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
