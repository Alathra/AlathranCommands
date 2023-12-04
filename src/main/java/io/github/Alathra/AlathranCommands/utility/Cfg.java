package io.github.Alathra.AlathranCommands.utility;

import com.github.milkdrinkers.Crate.Config;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.config.ConfigHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience class for accessing {@link ConfigHandler#getConfig}
 */
public abstract class Cfg {
    /**
     * Convenience method for {@link ConfigHandler#getConfig} to getConnection {@link Config}
     */
    @NotNull
    public static Config get() {
        return AlathranCommands.getInstance().getConfigHandler().getConfig();
    }
}
