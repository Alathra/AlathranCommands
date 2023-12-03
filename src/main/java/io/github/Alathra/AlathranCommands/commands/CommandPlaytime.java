package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.utils.MiscCfg;
import io.github.Alathra.AlathranCommands.utils.PlaytimeChecker;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandPlaytime {

    public CommandPlaytime(AlathranCommands pl) {
        new CommandAPICommand("playtime")
            .withPermission("alathrancommands.playtime")
            .withOptionalArguments(new OfflinePlayerArgument("checkedPlayer"))
            .executesPlayer((Player p, CommandArguments args) -> {
                if (args.get("checkedPlayer") == null) {
                    p.sendMessage(ColorParser.of(MiscCfg.get().getString("Playtime.Messages.Check-self")).parseMinimessagePlaceholder("playtime", PlaytimeChecker.playtimeString(p)).build());
                } else {
                    OfflinePlayer offlinePlayer = (OfflinePlayer) args.get("checkedPlayer");
                    p.sendMessage(ColorParser.of(MiscCfg.get().getString("Playtime.Messages.Check-other")).parseMinimessagePlaceholder("player", offlinePlayer.getName()).parseMinimessagePlaceholder("playtime", PlaytimeChecker.playtimeString(offlinePlayer)).build());
                }
        })
            .register();
    }

}
