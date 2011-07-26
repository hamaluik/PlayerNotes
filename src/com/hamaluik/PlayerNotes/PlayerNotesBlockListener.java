package com.hamaluik.PlayerNotes;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerNotesBlockListener extends BlockListener {
	private PlayerNotes plugin;
	
	// grab the main plug in so we can use it later
	public PlayerNotesBlockListener(PlayerNotes instance) {
		plugin = instance;
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		// get who broke the block..
		String name = event.getPlayer().getName();
		// update the stat..
		plugin.getPlayerStats(name, true).blocksBroken++;
		plugin.getPlayerStats(name, true).changed = true;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		// get who broke the block..
		String name = event.getPlayer().getName();
		// update the stat..
		plugin.getPlayerStats(name, true).blocksPlaced++;
		plugin.getPlayerStats(name, true).changed = true;
	}
}
