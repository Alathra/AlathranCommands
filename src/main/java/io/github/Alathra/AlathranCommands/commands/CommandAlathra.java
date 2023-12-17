package io.github.Alathra.AlathranCommands.commands;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import com.github.milkdrinkers.colorparser.ColorParser;

public class CommandAlathra {
    final private String prefix = "[ACMDS]";
    private final AlathranCommands alathraCommands;

    CommandAlathra(AlathranCommands pl) {
        alathraCommands = pl;
        alathraCommands.saveDefaultConfig();

        new CommandAPICommand("alathrancommands")
                .withSubcommands(
                    new CommandAPICommand("reload")
                        .withPermission(CommandPermission.fromString("alathrancommands.reload"))
                        .executes((sender, args) -> {
                            AlathranCommands.getInstance().onReload();
                            sender.sendMessage(ColorParser.of(prefix + " <green>config has been reloaded!").build());
                        }),
                    new CommandAPICommand("schedule")
                        .withPermission(CommandPermission.fromString("alathrancommands.schedule"))
                        .withSubcommands(
                            CommandStop.registerStopCommand(),
                            CommandStop.registerRestartCommand(),
                            CommandStop.registerCancelCommand()
                        )

                )
                .register();
    }
}
