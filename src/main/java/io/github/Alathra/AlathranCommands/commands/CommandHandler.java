package io.github.Alathra.AlathranCommands.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.Reloadable;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler implements Reloadable {
    private final AlathranCommands instance;

    public CommandHandler(AlathranCommands instance) {
        this.instance = instance;
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance).shouldHookPaperReload(true).silentLogs(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        // Register commands here
        new CommandAlathra(instance);
        new CommandTpa(instance);
        new CommandTpaHere(instance);
        new CommandTpCancel(instance);
        new CommandTpaccept(instance);
        new CommandTpaDeny(instance);
        new CommandTpo(instance);
        new CommandTpoHere(instance);
        new CommandTpToggle(instance);
        new CommandWildTp(instance);
        new CommandClearCooldowns(instance);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}
