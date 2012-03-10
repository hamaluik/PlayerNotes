package com.hamaluik.PlayerNotes;

import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerNotesPlayerListener implements Listener {
	private PlayerNotes plugin;
	
	// grab the main plug in so we can use it later
	public PlayerNotesPlayerListener(PlayerNotes instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {
		// get the player's stat
		String name = event.getPlayer().getName();
		Stat stat = plugin.getPlayerStats(name, true);
		stat.numKicks++;
		stat.changed = true;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		// get the player's stat
		String name = event.getPlayer().getName();
		Stat stat = plugin.getPlayerStats(name, true);
		stat.timeOnServer += (System.currentTimeMillis() / 1000) - stat.joinTime;
		stat.changed = true;
		// and remove them from internal tracking!
		plugin.unloadPlayerStat(name);
	}
}
