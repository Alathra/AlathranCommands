package com.jklmao.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jklmao.plugin.ClickTpa;
import com.jklmao.plugin.utils.TpaInfoList;

public class CommandTpaDeny implements CommandExecutor {

	private ClickTpa clicktpa;

	public CommandTpaDeny(ClickTpa pl) {
		this.clicktpa = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission("clicktpa.tpadeny")) {
				p.sendMessage(colorize(this.clicktpa.getConfig().getString("Insufficient-permission")));
				return true;
			}
			if (args.length == 0) {
				p.sendMessage(colorize(this.clicktpa.getConfig().getString("Tpdeny-usage")));
				return true;
			}
			final Player target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				p.sendMessage(colorize(this.clicktpa.getConfig().getString("No-player-found")));
				return true;
			}

			if (target.isOnline()) {

				boolean hasRequester = false;

				for (TpaInfoList list : clicktpa.getTpaPlayers().get(p).getTpaList()) {
					if (list.getRequester() == target) {
						clicktpa.getTpaPlayers().get(p).getTpaList().remove(list);
						clicktpa.getTpaCancel().remove(target);
						hasRequester = true;
						break;
					}
				}

				if (hasRequester) {
					p.sendMessage(colorize(this.clicktpa.getConfig().getString("Player-deny-tpa-message")));
					target.sendMessage(colorize(this.clicktpa.getConfig().getString("Target-deny-tpa-message")));
					clicktpa.getTpaCancel().remove(target);
					return true;
				} else {
					p.sendMessage(colorize(this.clicktpa.getConfig().getString("Player-no-pendingtpa-message")));
					return true;
				}

			} else {
				p.sendMessage(colorize(clicktpa.getConfig().getString("Target-is-offline")));
				return true;
			}
		}
		sender.sendMessage(colorize(this.clicktpa.getConfig().getString("Player-only-command")));
		return true;
	}

	public static String colorize(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

}
