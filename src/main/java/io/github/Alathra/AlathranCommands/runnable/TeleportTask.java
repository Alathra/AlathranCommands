package io.github.Alathra.AlathranCommands.runnable;

import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import io.github.Alathra.AlathranCommands.utils.TeleportUtils;

import java.util.concurrent.CompletableFuture;

public class TeleportTask implements Runnable {
    private final TPARequest request;
    private final CompletableFuture<Boolean> future;

    public TeleportTask(TPARequest request, CompletableFuture<Boolean> future) {
        this.request = request;
        this.future = future;
    }

    @Override
    public void run() {
        TeleportUtils.asyncTeleport(request, future);
    }
}
