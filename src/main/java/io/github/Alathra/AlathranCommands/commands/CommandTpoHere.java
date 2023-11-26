package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.utils.ConfigUtil;
import io.github.Alathra.AlathranCommands.utils.TPCfg;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class CommandTpoHere implements ConfigUtil {
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

                    p.sendMessage(ColorParser.of(getMsg("Player-currently-teleporting")).build());
                    target.teleportAsync(p.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    target.sendMessage(ColorParser.of(getMsg("Target-currently-teleporting")).build());
                })
                .register();
    }

    @Override
    public @Nullable String getMsg(String path) {
        return alathraCommands.getConfig().getString(path);
    }
}
