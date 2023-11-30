package io.github.Alathra.AlathranCommands;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.Alathra.AlathranCommands.commands.CommandHandler;
import io.github.Alathra.AlathranCommands.data.CooldownManager;
import io.github.Alathra.AlathranCommands.data.PlayerManager;
import io.github.Alathra.AlathranCommands.hooks.VaultHook;
import io.github.Alathra.AlathranCommands.listener.ListenerHandler;
import io.github.Alathra.AlathranCommands.utils.Logger;
import io.github.Alathra.AlathranCommands.config.TeleportConfigHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AlathranCommands extends JavaPlugin {
    private static AlathranCommands instance;
    private TeleportConfigHandler teleportConfigHandler;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private static VaultHook vaultHook;

    public static AlathranCommands getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        teleportConfigHandler = new TeleportConfigHandler(instance);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);
        vaultHook = new VaultHook(instance);

        teleportConfigHandler.onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
        vaultHook.onLoad();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        teleportConfigHandler.onEnable();
        commandHandler.onEnable();
        listenerHandler.onEnable();
        vaultHook.onEnable();

        // Economy Notification
        if (vaultHook.isVaultLoaded()) {
            Logger.get().info(ColorParser.of("<green>Vault has been found on this server. Economy support enabled.").build());
        } else {
            Logger.get().warn(ColorParser.of("<yellow>Vault is not installed on this server. Economy support has been disabled.").build());
        }

        Logger.get().info(ColorParser.of("<green>AlathranCommands successfully loaded.").build());
    }

    @Override
    public void onDisable() {
        commandHandler.onDisable();
        listenerHandler.onDisable();
        vaultHook.onDisable();
        teleportConfigHandler.onDisable();
        PlayerManager.getInstance().reset();
        CooldownManager.getInstance().reset();
    }

    @NotNull
    public TeleportConfigHandler getTeleportConfigHandler() {
        return teleportConfigHandler;
    }

    public static VaultHook getVaultHook() {
        return vaultHook;
    }
}
