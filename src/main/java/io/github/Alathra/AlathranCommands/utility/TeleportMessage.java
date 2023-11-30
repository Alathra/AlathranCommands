package io.github.Alathra.AlathranCommands.utility;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.Alathra.AlathranCommands.data.model.TPARequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * Utility class for sending teleport related messages to players.
 */
public class TeleportMessage {
    public static void requestSend(TPARequest request) {
        final Player origin = request.getOrigin();
        final Player target = request.getTarget();

        final Component requestSent = ColorParser.of(TPCfg.get().getString("Messages.request-sent-origin")).parseMinimessagePlaceholder("target", target.getName()).build();
        final Component accept = ColorParser.of(TPCfg.get().getString("Messages.request-click-accept")).parseStringPlaceholder("%username%", origin.getName()).build();
        final Component deny = ColorParser.of(TPCfg.get().getString("Messages.request-click-deny")).parseStringPlaceholder("%username%", origin.getName()).build();

        @Nullable String tpa = switch (request.getType()) {
            case TPA -> TPCfg.get().getString("Messages.request-sent-target-tpa");
            case TPAHERE -> TPCfg.get().getString("Messages.request-sent-target-tpahere");
            default -> null;
        };

        if (tpa != null) {
            target.sendMessage(
                ColorParser.of(tpa)
                    .parseMinimessagePlaceholder("player", origin.getName())
                    .parseMinimessagePlaceholder("accept", accept)
                    .parseMinimessagePlaceholder("deny", deny)
                    .build()
            );
        }

        origin.sendMessage(requestSent);
    }

    public static void requestTeleported(TPARequest request) {
        final Player origin = request.getOrigin();
        final Player target = request.getTarget();

        final String titleString = TPCfg.get().getString("Messages.teleporting-origin-title");
        final String subTitleString = TPCfg.get().getString("Messages.teleporting-origin-subtitle");

        final Component titleMain = ColorParser.of(titleString).build();
        final Component subMain = ColorParser.of(subTitleString).build();

        final Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(250));
        final Title title = Title.title(titleMain, subMain, times);

        switch (request.getType()) {
            case TPA -> {
                if (isTitlesEnabled()) {
                    origin.showTitle(title);
                }

                origin.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.teleporting-origin")).build());
                target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.teleporting-target")).parseMinimessagePlaceholder("origin", origin.getName()).build());
            }
            case TPAHERE ->  {
                if (isTitlesEnabled()) {
                    target.showTitle(title);
                }

                target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.teleporting-origin")).build());
                origin.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.teleporting-target")).parseMinimessagePlaceholder("origin", target.getName()).build());
            }
        }
    }

    public static void requestTeleporting(long secs, TPARequest request) {
        if (!isTitlesEnabled()) return;

        final String titleString = TPCfg.get().getString("Messages.grace-origin-title");
        final String subTitleString = TPCfg.get().getString("Messages.grace-origin-subtitle");

        final Component titleMain = ColorParser.of(titleString).build();
        final Component subMain = ColorParser.of(subTitleString).build();

        final Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(secs), Duration.ofMillis(250));
        final Title title = Title.title(titleMain, subMain, times);

        switch (request.getType()) {
            case TPA -> {
                request.getOrigin().showTitle(title);
            }
            case TPAHERE ->  {
                request.getTarget().showTitle(title);
            }
        }
    }

    public static void requestTeleportCancel(TPARequest request) {
        final Player origin = request.getOrigin();
        final Player target = request.getTarget();

        final String titleString = TPCfg.get().getString("Messages.error-origin-cancel-title");
        final String subTitleString = TPCfg.get().getString("Messages.error-origin-cancel-title");

        final Component titleMain = ColorParser.of(titleString).build();
        final Component subMain = ColorParser.of(subTitleString).build();

        final Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(250));
        final Title title = Title.title(titleMain, subMain, times);

        switch (request.getType()) {
            case TPA -> {
                if (isTitlesEnabled()) {
                    origin.showTitle(title);
                }

                origin.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-origin-cancel-moved")).parseMinimessagePlaceholder("player", origin.getName()).build());
                target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-target-cancel-moved")).parseMinimessagePlaceholder("player", origin.getName()).build());
            }
            case TPAHERE ->  {
                if (isTitlesEnabled()) {
                    target.showTitle(title);
                }

                target.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-origin-cancel-moved")).parseMinimessagePlaceholder("player", origin.getName()).build());
                origin.sendMessage(ColorParser.of(TPCfg.get().getString("Messages.error-target-cancel-moved")).parseMinimessagePlaceholder("player", origin.getName()).build());
            }
        }
    }

    private static boolean isTitlesEnabled() {
        return TPCfg.get().getBoolean("Settings.TPA.Titles");
    }
}
