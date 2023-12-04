package io.github.Alathra.AlathranCommands.utility;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.db.DatabaseHandler;
import io.github.Alathra.AlathranCommands.db.DatabaseType;
import io.github.Alathra.AlathranCommands.db.jooq.JooqContext;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Convenience class for accessing methods in {@link DatabaseHandler#getConnection}
 */
public abstract class DB {
    /**
     * Convenience method for {@link DatabaseHandler#getConnection} to getConnection {@link Connection}
     */
    @NotNull
    public static Connection getConnection() throws SQLException {
        return AlathranCommands.getInstance().getDataHandler().getConnection();
    }

    /**
     * Convenience method for {@link JooqContext#createContext(Connection)} to getConnection {@link DSLContext}
     */
    @NotNull
    public static DSLContext getContext(Connection con) {
        return AlathranCommands.getInstance().getDataHandler().getJooqContext().createContext(con);
    }

    /**
     * Convenience method for {@link DatabaseHandler#getDB()} to getConnection {@link DatabaseType}
     */
    public static DatabaseType getDB() {
        return AlathranCommands.getInstance().getDataHandler().getDB();
    }
}
