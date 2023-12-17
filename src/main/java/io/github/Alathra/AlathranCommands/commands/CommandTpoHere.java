package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.utility.ConfigUtil;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class CommandTpoHere {
    private final AlathranCommands alathraCommands;

    public CommandTpoHere(AlathranCommands pl) {
        alathraCommands = pl;

        new CommandAPICommand("tpohere")
                .withAliases("etpohere", "tphere")
                .withPermission(CommandPermission.fromString("alathrancommands.tpohere"))
                .withArguments(new PlayerArgument("target"))
                .executesPlayer((Player p, CommandArguments args) -> {
                    Player target = (Player) args.get("target");

                    if (p == target) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-istarget")).build());
                    }

                    p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.teleporting-origin")).build());
                    target.teleportAsync(p.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.teleporting-tpo-target")).build());
                })
                .register();
    }
}
