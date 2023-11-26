package io.github.Alathra.AlathranCommands.listener;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.Reloadable;
import io.github.Alathra.AlathranCommands.listener.player.JoinListener;
import io.github.Alathra.AlathranCommands.listener.player.QuitListener;
import org.bukkit.event.HandlerList;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {
    private final AlathranCommands instance;

    public ListenerHandler(AlathranCommands instance) {
        this.instance = instance;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        // Register listeners here
        instance.getServer().getPluginManager().registerEvents(new JoinListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new QuitListener(), instance);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(instance);
    }
}
