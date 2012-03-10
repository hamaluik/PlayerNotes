package com.hamaluik.PlayerNotes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerNotesBlockListener implements Listener {
	private PlayerNotes plugin;
	
	// grab the main plug in so we can use it later
	public PlayerNotesBlockListener(PlayerNotes instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		// get who broke the block..
		String name = event.getPlayer().getName();
		// update the stat..
		plugin.getPlayerStats(name, true).blocksBroken++;
		plugin.getPlayerStats(name, true).changed = true;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		// get who broke the block..
		String name = event.getPlayer().getName();
		// update the stat..
		plugin.getPlayerStats(name, true).blocksPlaced++;
		plugin.getPlayerStats(name, true).changed = true;
	}
}
