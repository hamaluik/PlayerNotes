package com.hamaluik.PlayerNotes.commands;


import org.bukkit.command.CommandSender;

import ru.tehkode.permissions.PermissionGroup;

import com.hamaluik.PlayerNotes.PlayerNotes;
import com.hamaluik.PlayerNotes.PlayerNotesCommandExecutor;


public class CommandCount implements Command {
	private PlayerNotes plugin;
	public CommandCount(PlayerNotes instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(args.length < 0) return false;
		
		//TODO fuzz: i wrote this MCNSA Only but below is i think server adaptable (the To Do is to get your attention
		
		/*
		//get number of players in a certain rank
		int Peon = plugin.permissions.getGroup("Peon").getUsers().length;
		int Guest = plugin.permissions.getGroup("Guest").getUsers().length;
		int Altarboy = plugin.permissions.getGroup("Altarboy").getUsers().length;
		int Priest = plugin.permissions.getGroup("Priest").getUsers().length;
		int Elder = plugin.permissions.getGroup("Elder").getUsers().length;
		int Cardinal = plugin.permissions.getGroup("Cardinal").getUsers().length;
		int Bishop = plugin.permissions.getGroup("Bishop").getUsers().length;
		int aVolition = plugin.permissions.getGroup("aVolition").getUsers().length;
		int Blessed = plugin.permissions.getGroup("Blessed").getUsers().length;
		int Saint = plugin.permissions.getGroup("Saint").getUsers().length;
		int Templar = plugin.permissions.getGroup("Templar").getUsers().length;
		int Angelic = plugin.permissions.getGroup("Angelic").getUsers().length;
		int HolyGhost = plugin.permissions.getGroup("Holy Ghost").getUsers().length;
		int God = plugin.permissions.getGroup("God").getUsers().length;

		//return it to the sender
		
		PlayerNotesCommandExecutor.returnMessage(sender, "----------Playercount----------");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Peon + "Peons");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Guest + "Guests");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Altarboy + "Altarboys");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Priest + "Priests");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Elder + "Elders");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Cardinal + "Cardinals");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Bishop + "Bishops");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + aVolition + "aVo members");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Blessed + "Blesseds");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Saint + "Saints");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Templar + "Templars");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + Angelic + "Angelics");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + HolyGhost + "Holy Ghost(s)");
		PlayerNotesCommandExecutor.returnMessage(sender, "We have " + God + "God(s)");
		
		*/
		
		int total = plugin.permissions.getGroups().length;
		PermissionGroup[] group = plugin.permissions.getGroups();
		String name = "";
		PlayerNotesCommandExecutor.returnMessage(sender, "----------Playercount----------");
		for(int i=0; i<total; i++){
			name = group[i].getName();
			int number = plugin.permissions.getGroup(group[i].getName()).getUsers().length;
			PlayerNotesCommandExecutor.returnMessage(sender, "We have " + number + " " + name + "s on this server");
       }
		return true;
	}
	
	
	
	
	public String requiredPermission() {
		return "playernotes.stats.other";
	}
	
	public String getCommand() {
		return "count";
	}
	
	
	public String getDescription() {
		return "get the total number of players per rank";
	}

	public String getArguments() {
		return null;
	}
}
