package io.github.Alathra.AlathranCommands.utils;

import org.jetbrains.annotations.Nullable;

@Deprecated
public interface ConfigUtil {
    @Deprecated // TODO All current usages (and the message strings) of this should be migrated from the default config to TeleportConfigHandler AKA TPCfg
    default @Nullable String getMsg(String path) {
        return null;
    }
}
