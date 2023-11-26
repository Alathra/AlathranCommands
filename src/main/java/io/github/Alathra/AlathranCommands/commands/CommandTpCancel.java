package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import io.github.Alathra.AlathranCommands.utils.TPCfg;
import org.bukkit.entity.Player;

public class CommandTpCancel {
    private final AlathranCommands alathraCommands;

    public CommandTpCancel(AlathranCommands pl) {
        alathraCommands = pl;

        new CommandAPICommand("tpacancel")
                .withAliases("etpacancel", "tpcancel")
                .withPermission(CommandPermission.fromString("alathrancommands.tpacancel"))
                .withOptionalArguments(new PlayerArgument("target"))
                .executesPlayer((Player p, CommandArguments args) -> {
                    if (PlayerManager.getInstance().getPlayer(p).getOutgoingTPARequests().isEmpty()) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outgoing-request")).build());
                    }

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

                    if (!PlayerManager.getInstance().getPlayer(p).getOutgoingTPARequests().contains(target.getUniqueId())) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outgoing-request")).build());
                    }

                    if (p == target) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-istarget")).build());
                    }

                    /*if (!PlayerManager.getInstance().getPlayer(p).isOutgoingTPARequest()) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outstanding-request")).build());
                    }

                    Player target = Bukkit.getServer().getPlayer(alathraCommands.getTpaCancel().get(p).getName());
                    Player target = Bukkit.getServer().getPlayer(PlayerManager.getInstance().getPlayer(p).getOutgoingTPARequests().);*/

                    /*if (target == null) {
                        alathraCommands.getTpaCancel().remove(p);
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.request-canceled-origin")).parseMinimessagePlaceholder("target", alathraCommands.getTpaCancel().get(p).getName()).build());
                    }*/

                    //removeTpaInfo(p, target);
                    TPARequest request = PlayerManager.getInstance().getPlayer(target).getOutstandingTpaRequest(p, false);

                    if (request == null || request.isProcessed()) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-no-outstanding-request")).build());
                    }

                    request.setProcessed(true);
                    target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-canceled-target")).parseMinimessagePlaceholder("origin", p.getName()).build());
                    p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-canceled-origin")).parseMinimessagePlaceholder("target", target.getName()).build());
                    PlayerManager.getInstance().getPlayer(request.getTarget()).removeTPARequest(request.getOrigin());
                    PlayerManager.getInstance().getPlayer(request.getOrigin()).removeOutgoingTPARequest(request.getTarget());

                    //removeTpaInfo(p, target);
                })
                .register();
    }
}
