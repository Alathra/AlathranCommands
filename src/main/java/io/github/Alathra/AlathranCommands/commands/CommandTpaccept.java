package io.github.Alathra.AlathranCommands.commands;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import io.github.Alathra.AlathranCommands.runnable.TeleportGraceTask;
import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.Alathra.AlathranCommands.utils.TPCfg;
import io.github.Alathra.AlathranCommands.utils.TeleportUtils;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class CommandTpaccept {
    private final AlathranCommands alathraCommands;

    public CommandTpaccept(AlathranCommands pl) {
        alathraCommands = pl;

        final int seconds = alathraCommands.getConfig().getInt("Seconds-until-tpa");

        new CommandAPICommand("tpaccept")
                .withAliases("etpaccept", "tpyes", "etpyes")
                .withPermission(CommandPermission.fromString("alathrancommands.tpaccept"))
                .withOptionalArguments(new PlayerArgument("target"))
                .executesPlayer((Player p, CommandArguments args) -> {
                    // Get optionally specified target
                    Player target;
                    if (args.get("target") != null) {
                        target = (Player) args.get("target");
                    } else if (PlayerManager.getInstance().getPlayer(p).getNextTpaRequest(false, false) != null) {
                        target = PlayerManager.getInstance().getPlayer(p).getNextTpaRequest(false, false).getTarget(); // This returns the wrong us
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

                    switch (request.getType()) {
                        case TPA -> {
                            if (PlayerManager.getInstance().getPlayer(request.getOrigin()).isBusy()) {
                                throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.request-canceled-isbusy")).build());
                            }
                        }
                        case TPAHERE -> {
                            if (PlayerManager.getInstance().getPlayer(request.getTarget()).isBusy()) {
                                throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of(TPCfg.get().getString("Messages.request-canceled-isbusy")).build());
                            }
                        }
                    }

                    switch (request.getType()) {
                        case TPA, TPAHERE -> {
                            request.getOrigin().sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-accepted-origin")).parseMinimessagePlaceholder("target", request.getTarget().getName()).build());
                            request.getTarget().sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-accepted-target")).parseMinimessagePlaceholder("origin", request.getOrigin().getName()).build());
                        }
                    }

                    final CompletableFuture<Boolean> future = new CompletableFuture<>();

                    // Remove requests
                    future.whenComplete((_result, _throwable) -> {
                        switch (request.getType()) {
                            case TPA -> {
                                if (PlayerManager.getInstance().getPlayer(request.getOrigin()) != null) {
                                    PlayerManager.getInstance().getPlayer(request.getOrigin()).setBusy(false);
                                };

                                // If a players data is missing it has already been cleaned up by logout logic
                                if (PlayerManager.getInstance().getPlayer(request.getOrigin()) == null || PlayerManager.getInstance().getPlayer(request.getTarget()) == null) break;

                                PlayerManager.getInstance().getPlayer(request.getTarget()).removeTPARequest(request.getTarget());
                                PlayerManager.getInstance().getPlayer(request.getOrigin()).removeOutgoingTPARequest(request.getTarget());
                            }
                            case TPAHERE -> {
                                if (PlayerManager.getInstance().getPlayer(request.getTarget()) != null) {
                                    PlayerManager.getInstance().getPlayer(request.getTarget()).setBusy(false);
                                };

                                // If a players data is missing it has already been cleaned up by logout logic
                                if (PlayerManager.getInstance().getPlayer(request.getOrigin()) == null || PlayerManager.getInstance().getPlayer(request.getTarget()) == null) break;

                                PlayerManager.getInstance().getPlayer(request.getTarget()).removeTPARequest(request.getOrigin());
                                PlayerManager.getInstance().getPlayer(request.getOrigin()).removeOutgoingTPARequest(request.getTarget());
                            }
                        }
                    });

                    request.setProcessed(true);

                    final long grace = Duration.ofSeconds(TPCfg.get().getInt("Settings.TPA.Grace.Time")).toMillis();

                    if (grace == 0) {
                        TeleportUtils.asyncTeleport(request, future);
                    } else {
                        new TeleportGraceTask(request, grace, future);
                    }

                    switch (request.getType()) {
                        case TPA -> {
                            PlayerManager.getInstance().getPlayer(request.getOrigin()).setBusy(true);

                            //target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-grace-begin-origin")).parseMinimessagePlaceholder("time", seconds).build());
                            /*alathraCommands.getTpaPlayers().get(target).setMode(TeleportMode.TELEPORTING);
                            alathraCommands.getGraceList().add(target);
                            cdListener.tpaCountdown(seconds, TeleportType.TPA, p, target);*/
                        }
                        case TPAHERE -> {
                            PlayerManager.getInstance().getPlayer(request.getTarget()).setBusy(true);
                            //p.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.request-grace-begin-origin")).parseMinimessagePlaceholder("time", seconds).build());
                            /*alathraCommands.getTpaPlayers().get(p).setMode(TeleportMode.TELEPORTING);
                            alathraCommands.getGraceList().add(p);
                            cdListener.tpaCountdown(seconds, TeleportType.TPAHERE, p, target);*/
                        }
                    }

                    //alathraCommands.getTpaCancel().remove(target);

                })
                .register();
    }
}