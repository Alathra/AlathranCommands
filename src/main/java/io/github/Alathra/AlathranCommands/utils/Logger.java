package io.github.Alathra.AlathranCommands.utils;


import io.github.Alathra.AlathranCommands.AlathranCommands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link AlathranCommands#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link AlathranCommands#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return AlathranCommands.getInstance().getComponentLogger();
    }
}
