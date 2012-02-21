package com.hamaluik.PlayerNotes;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class PlayerNotesEntityListener extends EntityListener {
	private PlayerNotes plugin;
	private static HashMap<String, String> lastDamager = new HashMap<String, String>();
	
	// grab the main plug in so we can use it later
	public PlayerNotesEntityListener(PlayerNotes instance) {
		plugin = instance;
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		// see if a player is dying
		if(event.getEntity() instanceof Player) {
			// it IS a player
			// get who!
			String name = ((Player)event.getEntity()).getName();
			// and update their stat
			plugin.getPlayerStats(name, true).deaths++;
			plugin.getPlayerStats(name, true).changed = true;
			
			// now check to see if their last damage was by another player..
			if(lastDamager.containsKey(name)) {
				// ok, another player killed them!
				// update that other player's stat
				String killer = lastDamager.get(name);
				plugin.getPlayerStats(killer, true).playersKilled++;
				plugin.getPlayerStats(killer, true).changed = true;
				// and clear this person from the last damager's list
				lastDamager.remove(name);
			}
		}
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) { 
		// make sure a player got damaged
		if(event.getEntity() instanceof Player) {
			// get the player's name
			String name = ((Player)event.getEntity()).getName();
			
			// see if another entity damaged them
			if(event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent)event;
				if(mobevent.getDamager() instanceof Player) {
					// a player shot a bow at them
					// store the last damager!
					lastDamager.put(name, ((Player)mobevent.getDamager()).getName());
				}
				else {
					// it was a ghast or skeleton shooting at them
					// clear them from the last damager doo-dad
					if(lastDamager.containsKey(name)) {
						lastDamager.remove(name);
					}
				}
			}
			// it wasn't another player attacking them for sure
			else {
				// clear them from the last damager doo-dad
				if(lastDamager.containsKey(name)) {
					lastDamager.remove(name);
				}
			}
		}
	}
}
