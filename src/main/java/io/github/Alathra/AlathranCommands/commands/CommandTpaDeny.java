package io.github.Alathra.AlathranCommands.commands;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.Alathra.AlathranCommands.utils.TPCfg;
import org.bukkit.entity.Player;

public class CommandTpaDeny {
    private final AlathranCommands alathraCommands;

    public CommandTpaDeny(AlathranCommands pl) {
        alathraCommands = pl;

        new CommandAPICommand("tpadeny")
                .withAliases("tpdeny", "etpdeny", "tpno", "etpno")
                .withPermission(CommandPermission.fromString("alathrancommands.tpadeny"))
                .withOptionalArguments(new PlayerArgument("target"))
                .executesPlayer((Player p, CommandArguments args) -> {
                    // Get optionally specified target
                    Player target;
                    if (args.get("target") != null) {
                        target = (Player) args.get("target");
                    } else if (PlayerManager.getInstance().getPlayer(p).getNextTpaRequest(false, false) != null) {
                        target = PlayerManager.getInstance().getPlayer(p).getNextTpaRequest(false, false).getTarget();
                    } else {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outstanding-request")).build());
                    }

                    if (target == null || !target.isOnline()) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-target-offline")).build());
                    }

                    if (p == target) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-istarget")).build());
                    }

                    if (!PlayerManager.getInstance().getPlayer(p).hasPendingTpaRequest(target)) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outstanding-request")).build());
                    }

                    TPARequest request = PlayerManager.getInstance().getPlayer(p).getOutstandingTpaRequest(target, false);

                    if (request == null || request.isProcessed()) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outstanding-request")).build());
                    }

//                    if (hasRequester) {
                    request.setProcessed(true);
                    p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-denied-origin")).parseMinimessagePlaceholder("target", p.getName()).build());
                    target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-denied-target")).build());
                    PlayerManager.getInstance().getPlayer(p).removeTPARequest(target);
                    PlayerManager.getInstance().getPlayer(target).removeOutgoingTPARequest(p);

                    //alathraCommands.getTpaCancel().remove(target);
//                    } else {
//                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outstanding-request")).build());
//                    }
                })
                .register();
    }
}
