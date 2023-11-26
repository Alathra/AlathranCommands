package io.github.Alathra.AlathranCommands.commands.interfaces;

import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class Command {
    public CompletableFuture<Boolean> getNewExceptionFuture(final CommandSender sender, final String commandLabel) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.exceptionally(e -> {
            e.printStackTrace();
            //showError(sender.getSender(), e, commandLabel);
            return false;
        });
        return future;
    }
}
