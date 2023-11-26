package io.github.Alathra.AlathranCommands.commands;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CommandStop {
    private enum ActionType {
        STOP,
        RESTART
    }

    public static CommandAPICommand registerStopCommand() {
        return new CommandAPICommand("stop")
            .withPermission(CommandPermission.fromString("alathrancommands.stop"))
            .withArguments(
                List.of(
                    new IntegerArgument("time")
                        .replaceSuggestions(
                            ArgumentSuggestions.strings(
                                List.of(
                                    "0",
                                    "5",
                                    "10",
                                    "15",
                                    "60"
                                )
                            )
                        )
                )
            )
            .executes((s, args) -> {
                if (isActionScheduled)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("").build());

                final int minutes = (int) args.getOrDefault("time", 5);

                actionType = ActionType.STOP;
                acitonDuration = Duration.ofMinutes(minutes);
                actionBeginTime = Instant.now();
                actionEndTime = Instant.now().plus(acitonDuration);
                scheduleStart();
            });
    }

    public static CommandAPICommand registerRestartCommand() {
        return new CommandAPICommand("restart")
            .withPermission(CommandPermission.fromString("alathrancommands.restart"))
            .withArguments(
                List.of(
                    new IntegerArgument("time")
                        .replaceSuggestions(
                            ArgumentSuggestions.strings(
                                List.of(
                                    "0",
                                    "5",
                                    "10",
                                    "15",
                                    "60"
                                )
                            )
                        )
                )
            )
            .executes((s, args) -> {
                if (isActionScheduled)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("").build());

                final int minutes = (int) args.getOrDefault("time", 5);

                actionType = ActionType.RESTART;
                acitonDuration = Duration.ofMinutes(minutes);
                actionBeginTime = Instant.now();
                actionEndTime = Instant.now().plus(acitonDuration);
                scheduleStart();
            });
    }

    public static CommandAPICommand registerCancelCommand() {
        return new CommandAPICommand("cancel")
            .withPermission(CommandPermission.fromString("alathrancommands.cancel"))
            .executes((s, args) -> {
                if (!isActionScheduled)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("").build());

                s.sendMessage(ColorParser.of("Cancelled").build());
                scheduleStop();
            });
    }

    private static boolean isActionScheduled = false;
    private static ActionType actionType;
    private static int actionTaskId = 0;
    private static Duration acitonDuration = Duration.ZERO;
    private static Instant actionBeginTime = Instant.EPOCH;
    private static Instant actionEndTime = Instant.EPOCH;

    private static void scheduleStop() {
        Bukkit.getScheduler().cancelTask(actionTaskId);
        actionType = ActionType.RESTART;
        actionTaskId = 0;
        acitonDuration = Duration.ZERO;
        actionBeginTime = Instant.EPOCH;
        actionEndTime = Instant.EPOCH;
        isActionScheduled = false;
    }

    private static void scheduleStart() {
        isActionScheduled = true;
        actionTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(AlathranCommands.getInstance(), () -> {
            final Instant currentTime = Instant.now();
            final Duration remainingTime = Duration.between(currentTime, actionBeginTime);
            final boolean shouldStop = currentTime.isAfter(actionEndTime) && !Bukkit.isStopping();

            if (shouldStop) {
                Bukkit.broadcast(ColorParser.of("&a[&cScheduler&a] Stopping the server...").build());
                Bukkit.shutdown();
                return;
            }

            // Announce minutes remaining
            if (remainingTime.toSecondsPart() == 0) {
                switch (Math.toIntExact(remainingTime.toMinutes())) {
                    case 30, 10, 5, 4, 3, 2, 1 -> {
                        Bukkit.broadcast(ColorParser.of("&a[&cScheduler&a] Stopping the server in <time> minutes.").build());
                        return;
                    }
                }
            }

            // Announce seconds remaining
            if (remainingTime.toMinutes() == 0) {
                switch (Math.toIntExact(remainingTime.toSeconds())) {
                    case 30, 20, 10, 5, 4, 3, 2, 1 -> {
                        Bukkit.broadcast(ColorParser.of("&a[&cScheduler&a] Stopping the server in <time> seconds.").build());
                        return;
                    }
                }
            }

        }, 0L, 20L);
    }
}
