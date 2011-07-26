package com.hamaluik.PlayerNotes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerNotesCommandExecutor implements CommandExecutor {
	private static PlayerNotes plugin;
	
	public PlayerNotesCommandExecutor(PlayerNotes instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("help")) {
				// show help!
				showHelp(sender);
				return true;
			}
		}
		if(plugin.commands.containsKey(label)) {
			boolean hasPermission = true;
			if(sender instanceof Player) {
				hasPermission = plugin.hasPermission((Player)sender, plugin.commands.get(label).requiredPermission());
			}
			if(hasPermission) {
				if(!plugin.commands.get(label).onCommand(sender, args)) returnMessage(sender, "&cInvalid command usage!");
			}
			else {
				returnMessage(sender, "&cYou don't have permission for that!");
			}
		}
		
		return true;
	}
	
	// return a formatted message to whoever send the command
	// (no colours for the console)
	public static void returnMessage(CommandSender sender, String message) {
		if(sender instanceof Player) {
			sender.sendMessage(plugin.processColours(message));
		}
		else {
			sender.sendMessage(plugin.stripColours(message));
		}
	}
	
	public void showHelp(CommandSender sender) {
		returnMessage(sender, "&f----- &6Notes Help &f-----");
		for(String command: plugin.commands.keySet()) {
			boolean hasPermission = true;
			if(sender instanceof Player) {
				hasPermission = plugin.hasPermission((Player)sender, plugin.commands.get(command).requiredPermission());
			}
			if(hasPermission) {
				returnMessage(sender, "&e/" + plugin.commands.get(command).getCommand() + " "
						+ "&f" + plugin.commands.get(command).getArguments());
				returnMessage(sender, "       &7" + plugin.commands.get(command).getDescription());
			}
		}
	}
}
