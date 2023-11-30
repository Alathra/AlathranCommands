package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import io.github.Alathra.AlathranCommands.utility.WildLocation;
import org.bukkit.entity.Player;

public class CommandWildTp {

    public CommandWildTp (AlathranCommands pl) {
        new CommandAPICommand("wildtp")
            .withAliases("wild", "rtp")
            .withPermission(CommandPermission.fromString("alathrancommands.wildtp"))
            .executesPlayer((Player p, CommandArguments args) -> {
                // Teleports player
                WildLocation.search(p).thenAccept(location -> {
                    if (location != null) {
                        p.teleport(location);
                    } else {
                        p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-wildtp-nosuitablelocation")).build());
                    }
                });
        })
            .register();
    }
}
