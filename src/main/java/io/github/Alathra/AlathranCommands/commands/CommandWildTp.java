package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.enums.CooldownType;
import io.github.Alathra.AlathranCommands.utility.PlaytimeChecker;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import io.github.Alathra.AlathranCommands.utility.WildLocation;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class CommandWildTp {

    private int wildPreCooldown = TPCfg.get().getInt("Settings.WildTP.Cooldown-pre");
    private int wildPostCooldown = TPCfg.get().getInt("Settings.WildTP.Cooldown-post");
    private int pricePreCooldown = TPCfg.get().getInt("Settings.WildTP.Price-pre");
    private int pricePostCooldown = TPCfg.get().getInt("Settings.WildTP.Price-post");

    public CommandWildTp (AlathranCommands pl) {
        new CommandAPICommand("wildtp")
            .withAliases("wild", "rtp")
            .withPermission(CommandPermission.fromString("alathrancommands.wildtp"))
            .executesPlayer((Player p, CommandArguments args) -> {

                boolean checkPlaytime = PlaytimeChecker.checkPlaytime(p);

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

                // Teleports player
                WildLocation.search(p).thenAccept(location -> {
                    if (location != null) {
                        p.teleport(location);
                        if (checkPlaytime) {
                            if (AlathranCommands.getVaultHook().isVaultLoaded())
                                AlathranCommands.getVaultHook().getVault().withdrawPlayer(p, pricePostCooldown);
                            CooldownManager.getInstance().setCooldown(p, CooldownType.TELEPORT_WILDERNESS, wildPostCooldown);
                        } else {
                            if (AlathranCommands.getVaultHook().isVaultLoaded())
                                AlathranCommands.getVaultHook().getVault().withdrawPlayer(p, pricePreCooldown);
                            CooldownManager.getInstance().setCooldown(p, CooldownType.TELEPORT_WILDERNESS, wildPreCooldown);
                        }
                    } else {
                        p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-wildtp-nosuitablelocation")).build());
                    }
                });
        })
            .register();
    }
}
