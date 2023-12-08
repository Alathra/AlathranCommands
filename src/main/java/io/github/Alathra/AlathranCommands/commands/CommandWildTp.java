package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.enums.CooldownType;
import io.github.Alathra.AlathranCommands.runnable.WildTeleportTask;
import io.github.Alathra.AlathranCommands.utility.PlaytimeChecker;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandWildTp {
    private final int pricePreCooldown = TPCfg.get().getInt("Settings.WildTP.Price-pre");
    private final int pricePostCooldown = TPCfg.get().getInt("Settings.WildTP.Price-post");
    private final boolean teleportCurrentWorld = TPCfg.get().getBoolean("Settings.WildTP.Teleport-current-world");
    private final List<String> allowedWorlds = TPCfg.get().getStringList("Settings.WildTP.Allowed-origin-worlds");


    public CommandWildTp (AlathranCommands pl) {
        new CommandAPICommand("wildtp")
            .withAliases("wild", "rtp")
            .withPermission(CommandPermission.fromString("alathrancommands.wildtp"))
            .executesPlayer((Player p, CommandArguments args) -> {

                boolean checkPlaytime = PlaytimeChecker.checkPlaytime(p);

                @Nullable World teleportWorld;

                // Checks if player is allowed to RTP from this world
                if (!teleportCurrentWorld && !allowedWorlds.contains(p.getWorld().getName())) {
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-wild-wrongworld")).build());
                }

                // Check if balance is *less* than cost
                if (AlathranCommands.getVaultHook().isVaultLoaded()) {
                    if (checkPlaytime) {
                        if (AlathranCommands.getVaultHook().getVault().getBalance(p) < pricePostCooldown) {
                            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-notenoughfunds")).parseMinimessagePlaceholder("price", String.valueOf(pricePostCooldown)).build());
                        } else if (AlathranCommands.getVaultHook().getVault().getBalance(p) < pricePreCooldown) {
                            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-notenoughfunds")).parseMinimessagePlaceholder("price", String.valueOf(pricePreCooldown)).build());
                        }
                    }
                }

                if (CooldownManager.getInstance().hasCooldown(p, CooldownType.TELEPORT_WILDERNESS)) {
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-cooldown")).parseMinimessagePlaceholder("cooldown", CooldownManager.getInstance().getRemainingCooldownString(p, CooldownType.TELEPORT_WILDERNESS)).build());
                }

                if (teleportCurrentWorld) {
                    teleportWorld = p.getWorld();
                } else {
                    teleportWorld = Bukkit.getWorld(TPCfg.get().getString("Settings.WildTP.Destination-world"));
                    if (teleportWorld == null) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-world-config")).build());
                    }
                }

                final long grace = Duration.ofSeconds(TPCfg.get().getInt("Settings.WildTP.Grace.Time")).toMillis();

                p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.wild-findinglocation")).build());
                new WildTeleportTask(p, grace, teleportWorld);
        })
            .register();
    }
}
