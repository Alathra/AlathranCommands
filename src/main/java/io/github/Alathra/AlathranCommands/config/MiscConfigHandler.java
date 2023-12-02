package io.github.Alathra.AlathranCommands.config;

import com.github.milkdrinkers.Crate.Config;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.Reloadable;

public class MiscConfigHandler implements Reloadable {
    private final AlathranCommands instance;
    private static Config cfg;

    public MiscConfigHandler(AlathranCommands instance) {
        this.instance = instance;
    }

    @Override
    public void onLoad() {
        cfg = new Config("misc", instance.getDataFolder().getPath(), instance.getResource("misc.yml"));
    }

    @Override
    public void onEnable() {
    }

    public void onDisable() {
    }

    public Config getConfig() {
        return cfg;
    }
}
