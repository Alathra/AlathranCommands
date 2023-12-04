package io.github.Alathra.AlathranCommands.utility;

import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import io.github.Alathra.AlathranCommands.events.TeleportEvent;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

public class TeleportUtils {
    // TODO Messages
    public static void asyncTeleport(TPARequest request, final CompletableFuture<Boolean> future) {
        if (request.isExpired()) {
            future.complete(false);
            return;
        }

        if (!request.getOrigin().isOnline()) {
            future.complete(false);
            return;
        }

        if (!request.getTarget().isOnline()) {
            future.complete(false);
            return;
        }

        final TeleportEvent e = new TeleportEvent();
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            future.complete(false);
            return;
        }

        TeleportMessage.requestTeleported(request);
        switch (request.getType()) {
            case TPA -> request.getOrigin().teleportAsync(request.getTarget().getLocation());
            case TPAHERE -> request.getTarget().teleportAsync(request.getOrigin().getLocation());
        }
        future.complete(true);
    }
}
