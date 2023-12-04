package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.db.DatabaseQueries;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import org.bukkit.OfflinePlayer;

public class CommandClearCooldowns {

    public CommandClearCooldowns(AlathranCommands pl) {
        new CommandAPICommand("clearcooldowns")
            .withArguments(new OfflinePlayerArgument("player"))
            .withPermission("alathrancommands.clearcooldowns")
            .executes((sender, args) -> {
                OfflinePlayer offlinePlayer = (OfflinePlayer) args.get("player");
                if (!offlinePlayer.hasPlayedBefore()) {
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-clear-playerhasnotplayed")).parseMinimessagePlaceholder("player", offlinePlayer.getName()).build());
                }

                if (offlinePlayer.isOnline()) {
                    CooldownManager.getInstance().clearCooldowns(offlinePlayer);
                    sender.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.clear-successful")).parseMinimessagePlaceholder("player", offlinePlayer.getName()).build());
                } else {
                    DatabaseQueries.saveCooldown(offlinePlayer);
                    sender.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.clear-successful")).parseMinimessagePlaceholder("player", offlinePlayer.getName()).build());
                }
            })
            .register();
    }

}
