package io.github.Alathra.AlathranCommands;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On getInstance load.
     */
    void onLoad();

    /**
     * On getInstance enable.
     */
    void onEnable();

    /**
     * On getInstance disable.
     */
    void onDisable();
}
