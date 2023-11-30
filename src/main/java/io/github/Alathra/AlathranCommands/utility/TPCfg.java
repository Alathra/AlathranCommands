package io.github.Alathra.AlathranCommands.utility;

import com.github.milkdrinkers.Crate.Config;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.config.TeleportConfigHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience class for accessing {@link TeleportConfigHandler#getConfig}
 */
public abstract class TPCfg {
    /**
     * Convenience method for {@link TeleportConfigHandler#getConfig} to getConnection {@link Config}
     */
    @NotNull
    public static Config get() {
        return AlathranCommands.getInstance().getTeleportConfigHandler().getConfig();
    }
}
