package io.github.Alathra.AlathranCommands.runnable;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.enums.CooldownType;
import io.github.Alathra.AlathranCommands.events.TeleportGraceEvent;
import io.github.Alathra.AlathranCommands.utility.PlaytimeChecker;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import io.github.Alathra.AlathranCommands.utility.WildLocation;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class WildTeleportTask implements Runnable {
    private final static boolean canMove = TPCfg.get().getBoolean("Settings.WildTP.Grace.Allow-movement");
    private final Location teleporterLocation;
    private final Player player;
    private final long timeStarted = System.currentTimeMillis();
    private final long timeGrace;
    private int taskId;
    private final CooldownManager cooldownManager = CooldownManager.getInstance();
    private int price = 0;
    private int wildCooldown = 0;
    private Economy economy;
    private World world;

    public WildTeleportTask(Player player, long timeGrace, World world) {
        this.player = player;
        this.timeGrace = timeGrace;

        boolean checkPlaytime = PlaytimeChecker.checkPlaytime(player);

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.economy = AlathranCommands.getVaultHook().getVault();
            if (checkPlaytime) {
                this.price = TPCfg.get().getInt("Settings.WildTP.Price-post");
            } else {
                this.price = TPCfg.get().getInt("Settings.WildTP.Price-pre");
            }
        }

        if (checkPlaytime) {
            this.wildCooldown = TPCfg.get().getInt("Settings.WildTP.Cooldown-post");
        } else {
            this.wildCooldown = TPCfg.get().getInt("Settings.WildTP.Cooldown-pre");
        }

        this.teleporterLocation = player.getLocation();

        this.world = world;

        final TeleportGraceEvent e = new TeleportGraceEvent();
        Bukkit.getServer().getPluginManager().callEvent(e);


        this.taskId = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AlathranCommands.getInstance(), this, 0, 10).getTaskId();
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancelTask();
            return;
        }

        final Location teleporter = player.getLocation();

        if (!canMove && teleporterLocation.distance(teleporter) > 0.3) {
            player.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-origin-cancel-moved")).build());
            cancelTask();
            return;
        }

        final long timeCurrent = System.currentTimeMillis();
        if (timeCurrent > timeStarted + timeGrace) {
            cancelTask();

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AlathranCommands.getInstance(), () -> {
                if (cooldownManager.hasCooldown(player, CooldownType.TELEPORT_WILDERNESS)) {
                    player.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-cooldown")).parseMinimessagePlaceholder("cooldown", CooldownManager.getInstance().getRemainingCooldownString(player, CooldownType.TELEPORT_PLAYER)).build());
                    return;
                }

                if (economy.getBalance(player) < price) {
                    player.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-notenoughfunds")).parseMinimessagePlaceholder("price", String.valueOf(price)).build());
                    return;
                }

                WildLocation.search(player, world).thenAccept(location -> {
                    if (location != null) {
                        economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), price);
                        cooldownManager.setCooldown(player, CooldownType.TELEPORT_WILDERNESS, wildCooldown);
                        player.teleport(location);
                    } else {
                        player.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-wildtp-nosuitablelocation")).build());
                    }
                });
            });
        }
    }

    void cancelTask() {
        if (this.taskId == -1) return;

        try {
            Bukkit.getServer().getScheduler().cancelTask(this.taskId);
        } finally {
            this.taskId = -1;
        }
    }
}
