package com.hamaluik.PlayerNotes;

import java.util.Date;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerNotesPlayerListener extends PlayerListener {
	private PlayerNotes plugin;
	
	// grab the main plug in so we can use it later
	public PlayerNotesPlayerListener(PlayerNotes instance) {
		plugin = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		// get the player's stat
		String name = event.getPlayer().getName();
		Stat stat = plugin.getPlayerStats(name, true);
		// update the join time and join numbers
		stat.joinTime = System.currentTimeMillis() / 1000;
		stat.lastLogin = new Date();
		stat.numJoins++;
		stat.changed = true;
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		// get the player's stat
		String name = event.getPlayer().getName();
		Stat stat = plugin.getPlayerStats(name, true);
		stat.numKicks++;
		stat.changed = true;
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		// get the player's stat
		String name = event.getPlayer().getName();
		Stat stat = plugin.getPlayerStats(name, true);
		stat.timeOnServer += (System.currentTimeMillis() / 1000) - stat.joinTime;
		stat.changed = true;
		// and remove them from internal tracking!
		plugin.unloadPlayerStat(name);
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		if(event.getMessage().startsWith("/modreq ") && event.getMessage().length() > 8) {
			// get the player's stat
			String name = event.getPlayer().getName();
			Stat stat = plugin.getPlayerStats(name, true);
			stat.modRequests++;
			stat.changed = true;
		}
	}
}
