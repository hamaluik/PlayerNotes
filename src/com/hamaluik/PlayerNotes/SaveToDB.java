package com.hamaluik.PlayerNotes;

public class SaveToDB implements Runnable {
	private PlayerNotes plugin;
	
	// grab the main plug in so we can use it later
	public SaveToDB(PlayerNotes instance) {
		plugin = instance;
	}
	
	@Override
	public void run() {
		plugin.log.info("[PlayerNotes] saving stats to database...");
		for(String player: plugin.playerStats.keySet()) {
			if(plugin.playerStats.get(player).changed) {
				plugin.setPlayerDBStat(player, plugin.playerStats.get(player));
				plugin.playerStats.get(player).changed = false;
			}
		}
		plugin.log.info("[PlayerNotes] saved!");
	}
}
