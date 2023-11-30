package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import io.github.Alathra.AlathranCommands.enums.TeleportMode;
import io.github.Alathra.AlathranCommands.enums.TeleportType;
import io.github.Alathra.AlathranCommands.utility.TPCfg;
import io.github.Alathra.AlathranCommands.utility.TeleportMessage;
import org.bukkit.entity.Player;

public class CommandTpa {

    public CommandTpa(AlathranCommands pl) {
        new CommandAPICommand("tpa")
                .withAliases("tpask")
                .withPermission(CommandPermission.fromString("alathrancommands.tpa"))
                .withArguments(new PlayerArgument("target"))
                .executesPlayer((Player p, CommandArguments args) -> {
                    Player target = (Player) args.get("target");

                    if (p == target) {
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-istarget")).build());
                    }

                    if (PlayerManager.getInstance().getPlayer(target).getTeleportMode() == TeleportMode.TPTOGGLE_ON) { // TODO Accepts requests
                        throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-target-toggled-off")).parseMinimessagePlaceholder("target", target.getName()).build());
                    }

                    // Check if currently teleporting
                    if (!PlayerManager.getInstance().getPlayer(p).isBusy()) {
                        // Already has outgoing request
                        if (PlayerManager.getInstance().getPlayer(p).getOutgoingTPARequests().contains(target.getUniqueId())) {
                            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-outstanding-request")).build());
                        }

                        // Checks if vault is loaded
                        if (AlathranCommands.getVaultHook().isVaultLoaded()) {
                            // If player's balance is *less* than price
                            double price = TPCfg.get().getInt("Settings.TPA.Price");
                            if (AlathranCommands.getVaultHook().getVault().getBalance(p) < price) {
                                throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-tpa-notenoughfunds").concat(String.valueOf(Math.round(price)))).build());
                            }
                        }

                        // Add incoming and outgoing request to users
                        TPARequest tpaRequest = PlayerManager.getInstance().getPlayer(target).addTPARequest(p, target, TeleportType.TPA);
                        PlayerManager.getInstance().getPlayer(p).addOutgoingTPARequest(target);
                        TeleportMessage.requestSend(tpaRequest);
                    }
                })
                .register();
    }
}
