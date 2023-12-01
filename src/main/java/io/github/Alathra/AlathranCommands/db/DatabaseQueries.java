package io.github.Alathra.AlathranCommands.db;

import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.enums.CooldownType;
import io.github.Alathra.AlathranCommands.utility.DB;
import io.github.Alathra.AlathranCommands.utility.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static io.github.Alathra.AlathranCommands.db.schema.Tables.COOLDOWNS;

/**
 * A holder class for all SQL queries
 */
public abstract class DatabaseQueries {
    /**
     * Fetch a players cooldown from DB.
     */
    @Nullable
    public static Instant getCooldown(Player p) {
        try (
            Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            Result<Record> result = context.select()
                .from(COOLDOWNS)
                .where(COOLDOWNS.UUID.equal(convertUUIDToBytes(p.getUniqueId())))
                .fetch();

            for (Record r : result) {
                if (r.get(COOLDOWNS.TYPE).equals(CooldownType.TELEPORT_PLAYER.toString())) {
                    return r.get(COOLDOWNS.TIME).toInstant(ZoneOffset.of(ZoneOffset.systemDefault().getId()));
                }
            }

        } catch (SQLException | DataAccessException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
        return null;
    }

    /**
     * Deletes a players cooldowns and saves any new ones (if they exist) to DB.
     * <p>
     * Execute this after modifying the cooldown cache of the player.
     */
    public static void saveCooldown(Player p) {
        try (
            Connection con = DB.getConnection()
        ) {
            DSLContext context = DB.getContext(con);

            savePlayerCooldown(p, context);
        } catch (SQLException | DataAccessException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }

    private static void savePlayerCooldown(Player p, DSLContext context) {
        if (CooldownManager.getInstance().hasCooldown(p, CooldownType.TELEPORT_PLAYER)) {
            final Instant cooldown = CooldownManager.getInstance().getCooldown(p, CooldownType.TELEPORT_PLAYER);

            context.deleteFrom(COOLDOWNS)
                .where(COOLDOWNS.UUID.equal(convertUUIDToBytes(p.getUniqueId())).and(COOLDOWNS.TYPE.equal(CooldownType.TELEPORT_PLAYER.toString())))
                .execute();

            if (cooldown != null) {
                context.insertInto(COOLDOWNS)
                    .set(COOLDOWNS.TYPE, CooldownType.TELEPORT_PLAYER.toString())
                    .set(COOLDOWNS.UUID, convertUUIDToBytes(p.getUniqueId()))
                    .set(COOLDOWNS.TIME, Timestamp.from(cooldown).toLocalDateTime())
                    .execute();
            }
        }
    }

    /**
     * Updates the cooldown data in DB of all online players.
     */
    public static void saveCooldowns() {
        try (
            Connection con = DB.getConnection();
        ) {
            DSLContext context = DB.getContext(con);

            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                savePlayerCooldown(p, context);
            }
        } catch (SQLException | DataAccessException e) {
            Logger.get().error("SQL Query threw an error!", e);
        }
    }

    public static byte[] convertUUIDToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static UUID convertBytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
}
