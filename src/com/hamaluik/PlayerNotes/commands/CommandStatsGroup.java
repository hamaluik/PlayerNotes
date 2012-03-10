package com.hamaluik.PlayerNotes.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hamaluik.PlayerNotes.PlayerNotes;
import com.hamaluik.PlayerNotes.PlayerNotesCommandExecutor;
import com.hamaluik.PlayerNotes.Stat;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;

public class CommandStatsGroup implements Command {
	private PlayerNotes plugin;
	public CommandStatsGroup(PlayerNotes instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, String[] args) {
		// Fuzz rewrite
		// make sure they gave us proper args
		if(args.length < 1) {
			return false;
		}
		
		// ok, now get the group
		String targetGroup = "";
		// re-construct the group name (including spaces)
		for(int i = 0; i < args.length; i++) {
			targetGroup += args[i] + " ";
		}
		targetGroup = targetGroup.trim();
		PermissionGroup group = plugin.permissions.getGroup(targetGroup);
		if(group == null) {
			// it was a bad group!
			return false;
		}
		
		// ok, now go through all the online players and see if they're in that group
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			// now get the PermissionsUser for this playr
			PermissionUser user = plugin.permissions.getUser(players[i]);
			
			// now see if they have our group!
			String[] groupNames = user.getGroupsNames();
			
			for(int j = 0; j < groupNames.length; j++) {
				// now check to see if they're in the actual group!
				if(groupNames[j].equalsIgnoreCase(group.getName())) {
					// they're in our group!
					// get their stats!
					Stat stat = plugin.getPlayerStats(players[i].getName(), false);
					if(stat != null) {
						// ok, they're in the system!
						// update the time on server
						if(stat.joinTime != -1) {
							stat.timeOnServer += (System.currentTimeMillis() / 1000) - stat.joinTime;
							stat.joinTime = System.currentTimeMillis() / 1000; // reset this so we don't add cumulative times
						}
						
						// and tell them the stats!
						PlayerNotesCommandExecutor.returnMessage(sender, "----------------------------");
						PlayerNotesCommandExecutor.returnMessage(sender, "&6Stats for &f"+stat.name+"&6:");
						// format the time on server string
						int days = (int)(stat.timeOnServer / 86400);
						int hours = (int)(stat.timeOnServer / 3600) - (days * 24);
						int minutes = (int)((stat.timeOnServer  / 60) - (days * 1440) - (hours * 60));
						int seconds = (int)stat.timeOnServer % 60;
						PlayerNotesCommandExecutor.returnMessage(sender, " &6Total time: &7" + days + "d" + hours + "h" + minutes + "m" + seconds + "s");
						PlayerNotesCommandExecutor.returnMessage(sender, " &6Blocks broken: &7" + stat.blocksBroken + " &6placed: &7" + stat.blocksPlaced);
					}
					else {
						// not in the system..
						PlayerNotesCommandExecutor.returnMessage(sender, "&c\"&f" + players[i].getName() + "&c\" isn't in the system yet!");
					}
				}
			}
		}
		
		// and we're done!
		return true;
		
		/*// get the target group
		String typedTarget = new String("");
		String target = new String("");
		if(args.length == 1) {
			typedTarget = args[0];
		}
		else if(args.length == 0 && sender instanceof Player) {
			typedTarget = ((Player)sender).getName();
		}
		else {
			return false;
		}
		//Make Array to store online players in
		Player[] onlinePlayerList;
		String[] onlinePlayerNames;
		String[] targetGroup;
		
		//memory for online players
		int number = plugin.getServer().getOnlinePlayers().length;
		onlinePlayerList = new Player[number];
		onlinePlayerNames = new String[number];
		targetGroup =  new String[number];
		
		//put online players in list
		onlinePlayerList = plugin.getServer().getOnlinePlayers();
		//Loop through all the players in the list to get name
		for(int i=0; i<onlinePlayerList.length; i++){
				onlinePlayerNames[i] = onlinePlayerList[i].getName();		
		}
		//get their rank and sort accordingly
		int counter = 0;
		for(int i=0; i < targetGroup.length; i++) {
			if(PermissionsEx.getPermissionManager().getUser(onlinePlayerNames[i]).inGroup(typedTarget) == true) {
				targetGroup[counter] = onlinePlayerNames[i];
				counter++;
			}
		}
		//get stats for every player
		for(int i=0; i < targetGroup.length; i++) {
			// now lookup the target's actual name..
			if(plugin.getServer().getPlayer(typedTarget) != null) {
				// the player is online, set their name
				target = plugin.getServer().getPlayer(targetGroup[i]).getName();
			}
			else {
				target = typedTarget;
			}
			
			// ok, we have the target, get their stats!
			Stat stat = plugin.getPlayerStats(target, false);
			if(stat != null) {
				// ok, they're in the system!
				// update the time on server
				if(stat.joinTime != -1) {
					stat.timeOnServer += (System.currentTimeMillis() / 1000) - stat.joinTime;
					stat.joinTime = System.currentTimeMillis() / 1000; // reset this so we don't add cumulative times
				}
				
				// and tell them the stats!
				PlayerNotesCommandExecutor.returnMessage(sender, "&6Stats for &f"+stat.name+"&6:");
				// format the time on server string
				int days = (int)(stat.timeOnServer / 86400);
				int hours = (int)(stat.timeOnServer / 3600) - (days * 24);
				int minutes = (int)((stat.timeOnServer  / 60) - (days * 1440) - (hours * 60));
				int seconds = (int)stat.timeOnServer % 60;
				PlayerNotesCommandExecutor.returnMessage(sender, " &6Total time: &7" + days + "d" + hours + "h" + minutes + "m" + seconds + "s");
				PlayerNotesCommandExecutor.returnMessage(sender, " &6Blocks broken: &7" + stat.blocksBroken + " &6placed: &7" + stat.blocksPlaced);
				PlayerNotesCommandExecutor.returnMessage(sender, "----------------------------");
			}
			else {
				// not in the system..
				PlayerNotesCommandExecutor.returnMessage(sender, "&c\"&f" + target + "&c\" isn't in the system yet!");
			}
		}
		return true;*/
	}
	
	public String requiredPermission() {
		return "playernotes.stats";
	}
	
	public String getCommand() {
		return "statsg";
	}
	
	public String getArguments() {
		return "<group>";
	}
	
	public String getDescription() {
		return "get the stats for online <group>";
	}
}