package io.github.Alathra.AlathranCommands.config;

import com.github.milkdrinkers.Crate.Config;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.Reloadable;

public class TeleportConfigHandler implements Reloadable {
    private final AlathranCommands instance;
    private static Config cfg;

    public TeleportConfigHandler(AlathranCommands instance) {
        this.instance = instance;
    }

    @Override
    public void onLoad() {
        cfg = new Config("teleport", instance.getDataFolder().getPath(), instance.getResource("teleport.yml")); // Create a config file from the template in our resources folder
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    /**
     * Gets AlathranCommands Teleport config object.
     *
     * @return the Teleport config object
     */
    public Config getConfig() {
        return cfg;
    }
}
