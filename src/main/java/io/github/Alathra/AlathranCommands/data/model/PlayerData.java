package io.github.Alathra.AlathranCommands.data.model;

import io.github.Alathra.AlathranCommands.enums.TeleportMode;
import io.github.Alathra.AlathranCommands.enums.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerData {
    private static final LinkedHashMap<UUID, TPARequest> teleportRequestQueue = new LinkedHashMap<>(); // Store requests by target UUID
    private static final LinkedList<UUID> outgoingTPARequests = new LinkedList<>(); // Store UUID of targets
    private TeleportMode teleportMode = TeleportMode.DEFAULT;
    private boolean busy = false;

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean hasTPARequestFrom(final UUID uuid) {
        return teleportRequestQueue.containsKey(uuid);
    }

    public boolean hasTPARequestFrom(final Player p) {
        return hasTPARequestFrom(p.getUniqueId());
    }

    /**
     * Add a teleport request
     *
     * @param origin The player that is teleported
     * @param target The player being teleported to
     * @param type Teleport type
     *
     * @apiNote Use `type` to determine who is the tp requester
     */
    public TPARequest addTPARequest(final Player origin, final Player target, final TeleportType type) {
        final TPARequest request = new TPARequest(origin, target, type);

        // Handle max queue size
        teleportRequestQueue.remove(request.getOrigin().getUniqueId());
        if (teleportRequestQueue.size() >= 10) {
            final List<UUID> keys = new ArrayList<>(teleportRequestQueue.keySet());
            teleportRequestQueue.remove(keys.get(keys.size() - 1));
        }

        // Add request to queue
        teleportRequestQueue.put(request.getOrigin().getUniqueId(), request);

        return request;
    }

    /**
     * Remove request from the specified UUID
     *
     * @param uuid uuid
     *
     * @return removed request
     */
    public TPARequest removeTPARequest(final UUID uuid) {
        return teleportRequestQueue.remove(uuid);
    }

    public TPARequest removeTPARequest(final Player p) {
        return removeTPARequest(p.getUniqueId());
    }

    /**
     * Check if player has any pending tpa requests
     *
     * @param notify boolean, should we send a message to inform player of timed out request
     *
     * @return boolean
     */
    public boolean hasPendingTpaRequests(final boolean notify) {
        return getNextTpaRequest(notify, false) != null;
    }

    /**
     * Check if player has a pending tpa request
     *
     * @param uuid uuid
     *
     * @return boolean
     */
    public boolean hasPendingTpaRequest(final UUID uuid) {
        final TPARequest request = getOutstandingTpaRequest(uuid, false);
        return request != null;
    }

    public boolean hasPendingTpaRequest(final Player p) {
        return hasPendingTpaRequest(p.getUniqueId());
    }

    /**
     * Returns null if request doesn't exist or is timed out
     *
     * @param uuid uuid
     * @param notify boolean, should we send a message to inform player of timed out request
     *
     * @return request or null
     */
    public @Nullable TPARequest getOutstandingTpaRequest(final UUID uuid, final boolean notify) {
        if (!teleportRequestQueue.containsKey(uuid)) {
            return null;
        }

        final TPARequest request = teleportRequestQueue.get(uuid);

        if (!request.isExpired()) {
            return request;
        }

        removeTPARequest(uuid);

        if (notify) {
            final Player p = Bukkit.getPlayer(uuid);
            p.sendMessage("Request timed out %s"); // TODO send msg TeleportConfigHandler requestTimedOutFrom
        }

        return null;
    }

    public @Nullable TPARequest getOutstandingTpaRequest(final Player p, final boolean notify) {
        return getOutstandingTpaRequest(p.getUniqueId(), notify);
    }

    /**
     * Loop through requests from oldest to newest to find the most recent valid request
     *
     * @param notify boolean, should we send a message to inform player of timed out request
     * @param ignoreExpirations boolean, will return no longer valid requests
     *
     * @return request or null
     */
    public @Nullable TPARequest getNextTpaRequest(final boolean notify, final boolean ignoreExpirations) {
        if (teleportRequestQueue.isEmpty()) return null;

        final List<UUID> keys = new ArrayList<>(teleportRequestQueue.keySet());
        Collections.reverse(keys);

        TPARequest nextRequest = null;

        for (final UUID uuid : keys) {
            final TPARequest request = teleportRequestQueue.get(uuid);

            if (!request.isExpired()) {
                if (ignoreExpirations) {
                    return request;
                } else if (nextRequest == null) {
                    nextRequest = request;
                }
            } else {
                if (notify) {
                    final Player p = Bukkit.getPlayer(uuid);
                    p.sendMessage("Request timed out %s"); // TODO send msg TeleportConfigHandler requestTimedOutFrom
                    //sendMessage(tl("requestTimedOutFrom", ess.getUser(request.getRequesterUuid()).getDisplayName()));
                }
                removeTPARequest(uuid);
            }
        }

        return nextRequest;
    }

    public LinkedList<UUID> getOutgoingTPARequests() {
        return outgoingTPARequests;
    }

    public void addOutgoingTPARequest(UUID uuid) {
        outgoingTPARequests.add(uuid);
    }

    public void addOutgoingTPARequest(Player p) {
        addOutgoingTPARequest(p.getUniqueId());
    }

    public void removeOutgoingTPARequest(UUID uuid) {
        outgoingTPARequests.remove(uuid);
    }

    public void removeOutgoingTPARequest(Player p) {
        removeOutgoingTPARequest(p.getUniqueId());
    }

    public TeleportMode getTeleportMode() {
        return teleportMode;
    }

    public void setTeleportMode(TeleportMode mode) {
        teleportMode = mode;
    }
}
