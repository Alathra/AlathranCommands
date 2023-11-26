package io.github.Alathra.AlathranCommands.hooks;

import io.github.Alathra.AlathranCommands.AlathranCommands;
import io.github.Alathra.AlathranCommands.Reloadable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements Reloadable {
    private final AlathranCommands instance;
    private RegisteredServiceProvider<Economy> rsp;

    public VaultHook(AlathranCommands instance) {
        this.instance = instance;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        if (!instance.getServer().getPluginManager().isPluginEnabled("Vault"))
            return;

        setVault(instance.getServer().getServicesManager().getRegistration(Economy.class));
    }

    @Override
    public void onDisable() {
        setVault(null);
    }

    public boolean isVaultLoaded() {
        return rsp != null;
    }

    /**
     * Gets vault. Should only be used following {@link #isVaultLoaded()}.
     *
     * @return vault instance
     */
    public Economy getVault() {
        return rsp.getProvider();
    }

    private void setVault(RegisteredServiceProvider<Economy> rsp) {
        this.rsp = rsp;
    }
}
