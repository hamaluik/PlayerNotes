package com.hamaluik.PlayerNotes.commands;

import java.text.SimpleDateFormat;

import org.bukkit.command.CommandSender;

import com.hamaluik.PlayerNotes.PlayerNotes;
import com.hamaluik.PlayerNotes.PlayerNotesCommandExecutor;
import com.hamaluik.PlayerNotes.Stat;

public class CommandStats implements Command {
	private PlayerNotes plugin;
	public CommandStats(PlayerNotes instance) { plugin = instance; }
	
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
		
		// ok, we have the target, get their stats!
		Stat stat = plugin.getPlayerStats(target, false);
		// update the time on server
		stat.timeOnServer += (System.currentTimeMillis() / 1000) - stat.joinTime;
		stat.joinTime = System.currentTimeMillis() / 1000; // reset this so we don't add cumulative times
		
		// and tell them the stats!
		PlayerNotesCommandExecutor.returnMessage(sender, "&6Stats for &f"+stat.name+"&6:");
		// format the time on server string
		int days = (int)(stat.timeOnServer / 86400);
		int hours = (int)(stat.timeOnServer / 3600) - (days * 24);
		int minutes = (int)((stat.timeOnServer  / 60) - (days * 1440) - (hours * 60));
		int seconds = (int)stat.timeOnServer % 60;
		String date = new SimpleDateFormat("yyyy-MM-dd").format(stat.dateJoined);
		PlayerNotesCommandExecutor.returnMessage(sender, " &6Joined: &7" + date + " &6Total time: &7" + days + "d" + hours + "h" + minutes + "m" + seconds + "s");
		PlayerNotesCommandExecutor.returnMessage(sender, " &6Logins: &7" + stat.numJoins + " &6Times kicked: &7" + stat.numKicks);
		PlayerNotesCommandExecutor.returnMessage(sender, " &6Blocks broken: &7" + stat.blocksBroken + " &6placed: &7" + stat.blocksPlaced);
		PlayerNotesCommandExecutor.returnMessage(sender, " &6Players killed: &7" + stat.playersKilled + " &6Deaths: &7" + stat.deaths);
		
		return true;
	}
	
	public String requiredPermission() {
		return "playernotes.stats";
	}
	
	public String getCommand() {
		return "stats";
	}
	
	public String getArguments() {
		return "<player>";
	}
	
	public String getDescription() {
		return "get the stats for <player>";
	}
}
