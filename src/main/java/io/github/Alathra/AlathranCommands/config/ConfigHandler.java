package io.github.Alathra.AlathranCommands.config;

import com.github.milkdrinkers.Crate.Config;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.Reloadable;

import javax.inject.Singleton;

/**
 * A class that generates/loads & provides access to a configuration file.
 */
@Singleton
public class ConfigHandler implements Reloadable {
    private final AlathranCommands instance;
    private Config cfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param instance the plugin instance
     */
    public ConfigHandler(AlathranCommands instance) {
        this.instance = instance;
    }

    @Override
    public void onLoad() {
        cfg = new Config("config", instance.getDataFolder().getPath(), instance.getResource("config.yml")); // Create a config file from the template in our resources folder
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    /**
     * Gets examplePlugin config object.
     *
     * @return the examplePlugin config object
     */
    public Config getConfig() {
        return cfg;
    }
}
