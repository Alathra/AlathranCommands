package io.github.Alathra.AlathranCommands.commands;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import io.github.Alathra.AlathranCommands.enums.TeleportMode;
import io.github.Alathra.AlathranCommands.enums.TeleportType;
import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.Alathra.AlathranCommands.config.TeleportConfigHandler;
import io.github.Alathra.AlathranCommands.utils.TPCfg;
import io.github.Alathra.AlathranCommands.utils.TeleportMessage;
import org.bukkit.entity.Player;

public class CommandTpaHere {
    public CommandTpaHere(AlathranCommands pl) {
        new CommandAPICommand("tpahere")
            .withAliases("etpahere")
            .withPermission(CommandPermission.fromString("alathrancommands.tpahere"))
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
                            throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.error-origin-tpahere-notenoughfunds").concat(String.valueOf(Math.round(price)))).build());
                        }
                    }

                    // Add incoming and outgoing request to users
                    TPARequest tpaRequest = PlayerManager.getInstance().getPlayer(target).addTPARequest(p, target, TeleportType.TPAHERE);
                    PlayerManager.getInstance().getPlayer(p).addOutgoingTPARequest(target);
                    TeleportMessage.requestSend(tpaRequest);
                }
            })
            .register();
    }
}
