// HI Back

package com.hamaluik.PlayerNotes;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.hamaluik.PlayerNotes.commands.*;

public class PlayerNotes extends JavaPlugin {
	// the basics
	Logger log = Logger.getLogger("Minecraft");
	public PermissionManager permissions = null;
	
	// the database manager..
	public DBManager dbm = new DBManager(this);
	
	// data..
	public HashMap<String, Stat> playerStats = new HashMap<String, Stat>();
	
	// the commands..
	public HashMap<String, Command> commands = new HashMap<String, Command>();
	
	// the listeners
	PlayerNotesPlayerListener playerListener;
	PlayerNotesEntityListener entityListener;
	PlayerNotesBlockListener blockListener;
	PlayerNotesCommandExecutor commandExecutor;
	
	// the scheduled task
	SaveToDB statsDump = new SaveToDB(this);
	
	// options
	boolean useMYSQL = true;
	public boolean hasModTRS = false;
	String databaseName;
	String mysqlHost;
	String mysqlUser;
	String mysqlPass;
	private long statsDumpInterval;
	
	// startup routine..
	public void onEnable() {		
		// set up the plugin..
		this.setupPermissions();
		this.loadConfiguration();
		this.saveConfiguration();
		
		// ensure the database table exists..
		dbm.ensureTablesExist();
		
		// setup the listeners
		playerListener = new PlayerNotesPlayerListener(this);
		entityListener = new PlayerNotesEntityListener(this);
		blockListener = new PlayerNotesBlockListener(this);
		commandExecutor = new PlayerNotesCommandExecutor(this);
		
		// register commands
		registerCommand(new CommandNote(this));
		registerCommand(new CommandNoteDelete(this));
		registerCommand(new CommandNotes(this));
		registerCommand(new CommandStats(this));
		registerCommand(new CommandStatsGroup(this));
		
		// load the "join times" for any players currently on the server
		// (in case of reload)
		Player[] players = this.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			getPlayerStats(players[i].getName(), true).joinTime = System.currentTimeMillis() / 1000;
			getPlayerStats(players[i].getName(), true).changed = true;
		}
		
		// schedule the database saving
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, statsDump, statsDumpInterval * 20, statsDumpInterval * 20);
		log.info("[PlayerNotes] dumping to database every " + statsDumpInterval + " seconds");
		
		log.info("[PlayerNotes] plugin enabled");
	}

	// shutdown routine
	public void onDisable() {
		// save the player stats
		log.info("[PlayerNotes] saving stats to database...");
		for(String player: playerStats.keySet()) {
			setPlayerDBStat(player, playerStats.get(player));
		}
		playerStats.clear();
		log.info("[PlayerNotes] plugin disabled");
	}
	
	// register a command
	private void registerCommand(Command command) {
		// add the command to the commands list and register it with Bukkit
		this.commands.put(command.getCommand(), command);
		this.getCommand(command.getCommand()).setExecutor(this.commandExecutor);
	}
	
	// keep internal track of player stats..
	public Stat getPlayerStats(String player, boolean create) {
		// if player is already being tracked internally, grab the internal one
		if(playerStats.containsKey(player)) return playerStats.get(player);

		// if they're not already being kept track of, query their data
		Stat stat = dbm.getStat(player);
		if(create) playerStats.put(player, stat);
		return stat;
	}
	
	public void setPlayerStat(String player, Stat newStat) {
		// update the internal record
		playerStats.put(player, newStat);
	}
	
	public void setPlayerDBStat(String player, Stat newStat) {
		// update the database
		dbm.setStat(player, newStat);
	}
	
	public void unloadPlayerStat(String player) {
		// make sure the player IS loaded
		if(!playerStats.containsKey(player)) return;
		// update the database..
		dbm.setStat(player, playerStats.get(player));
		// and remove the player from internal tracking!
		playerStats.remove(player);
	}
	
	// load the permissions plugin..
	private void setupPermissions() {
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
			this.permissions = PermissionsEx.getPermissionManager();
			log.info("[PlayerNotes] permissions successfully loaded!");
		}
		else {
			log.info("[PlayerNotes] ERROR: PermissionsEx not found!");
		}
	}
	
	// just an interface function for checking permissions
	// if permissions are down, default to OP status.
	public boolean hasPermission(Player player, String permission) {
		if(permissions != null) {
			return permissions.has(player, permission);
		}
		else {
			return player.isOp();
		}
	}
	
	public void loadConfiguration() {
		this.getConfig().options().copyDefaults(true);
		FileConfiguration config = this.getConfig();
		
		String database = config.getString("database");
		if(database.equalsIgnoreCase("mysql")) useMYSQL = true;
		else useMYSQL = false;
		databaseName = config.getString("database-name");
		mysqlHost = config.getString("mysql-host");
		mysqlUser = config.getString("mysql-user");
		mysqlPass = config.getString("mysql-pass");
		statsDumpInterval = config.getInt("stats-dump-interval", 5) * 60;
	}
	
	public void saveConfiguration() {
		this.saveConfig();
	}
	
	// allow for colour tags to be used in strings..
	public String processColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	
	// strip colour tags from strings..
	public String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "");
	}
}
