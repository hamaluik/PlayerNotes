package com.hamaluik.PlayerNotes.commands;

import org.bukkit.command.CommandSender;

import com.hamaluik.PlayerNotes.PlayerNotes;
import com.hamaluik.PlayerNotes.PlayerNotesCommandExecutor;

public class CommandRank implements Command {
	private PlayerNotes plugin;
	public CommandRank(PlayerNotes instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, String[] args) {
		// get the target player
		String typedTarget = new String("");
		String target = new String("");
		if(args.length == 1) {
			typedTarget = args[0];
		}
		else {
			return false;
		}
		
		// now lookup the target's actual name..
		if(plugin.getServer().getPlayer(typedTarget) != null) {
			// the player is online, set their name
			target = plugin.getServer().getPlayer(typedTarget).getName();
		}
		else {
			target = typedTarget;
		}
		
		String[] groups = plugin.getPlayerGroups(target);
		if(groups == null || groups.length < 1) {
			PlayerNotesCommandExecutor.returnMessage(sender, "&f"+target+" &cdoes not have a rank!");
		}
		else {
			PlayerNotesCommandExecutor.returnMessage(sender, "&f"+target+"&3's rank is: &f" + groups[0]);
		}
		
		return true;
	}
	
	public String requiredPermission() {
		return "playernotes.rank";
	}
	
	public String getCommand() {
		return "rank";
	}
	
	public String getArguments() {
		return "<player>";
	}
	
	public String getDescription() {
		return "queries the rank/group from Permissions for <player>";
	}

}
