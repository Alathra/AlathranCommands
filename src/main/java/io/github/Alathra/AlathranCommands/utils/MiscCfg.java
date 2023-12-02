package io.github.Alathra.AlathranCommands.utils;

import com.github.milkdrinkers.Crate.Config;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import org.jetbrains.annotations.NotNull;

public abstract class MiscCfg {

    @NotNull
    public static Config get() {
        return AlathranCommands.getInstance().getMiscConfigHandler().getConfig();
    }
}
