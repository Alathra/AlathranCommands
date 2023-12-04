package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.enums.TeleportMode;
import io.github.Alathra.AlathranCommands.utility.TPCfg;

public class CommandTpToggle {
    private final AlathranCommands alathraCommands;

    public CommandTpToggle(AlathranCommands pl) {
        alathraCommands = pl;

        new CommandAPICommand("tptoggle")
                .withAliases("etptoggle")
                .withPermission(CommandPermission.fromString("alathrancommands.tptoggle"))
                .executesPlayer((p, args) -> {
                    TeleportMode mode = PlayerManager.getInstance().getPlayer(p).getTeleportMode();

                    switch (mode) {
                        case DEFAULT -> { // turn on tptoggle
                            PlayerManager.getInstance().getPlayer(p).setTeleportMode(TeleportMode.TPTOGGLE_ON);
                            p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.tptoggle-on")).build());
                        }
                        case TPTOGGLE_ON -> {
                            PlayerManager.getInstance().getPlayer(p).setTeleportMode(TeleportMode.DEFAULT);
                            p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.tptoggle-off")).build());
                        }
                        case TELEPORTING ->
                                p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.tptoggle-busy")).build());
                        default -> {
                        }
                    }
                })
                .register();
    }
}
