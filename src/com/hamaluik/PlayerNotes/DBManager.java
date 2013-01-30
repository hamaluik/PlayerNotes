package com.hamaluik.PlayerNotes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;

public class DBManager {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private PlayerNotes plugin;
	
	public DBManager(PlayerNotes instance) { plugin = instance; }
	
	public void connect() throws Exception {
		try {
			if(plugin.useMYSQL) {
				Class.forName("com.mysql.jdbc.Driver");
				connect = DriverManager.getConnection("jdbc:mysql://" + plugin.mysqlHost + "/"+plugin.databaseName+"?"
						+ "user="+plugin.mysqlUser+"&password="+plugin.mysqlPass);
			}
			else {
				Class.forName("org.sqlite.JDBC");
				connect = DriverManager.getConnection("jdbc:sqlite:"+plugin.databaseName);
			}
		}
		catch(Exception e) {
			throw e;
		}
	}
	
	public void ensureTablesExist() {
		try {
			// load the database
			connect();

			// create the tables if they does not exist
			preparedStatement = connect.prepareStatement("CREATE TABLE IF NOT EXISTS notes ( id INTEGER NOT NULL PRIMARY KEY AUTO"+(plugin.useMYSQL?"_":"")+"INCREMENT UNIQUE, date DATE NOT NULL, noteTaker TINYTEXT NOT NULL, notee TINYTEXT NOT NULL, note MEDIUMTEXT NOT NULL );");
			preparedStatement.executeUpdate();
			preparedStatement = connect.prepareStatement("CREATE TABLE IF NOT EXISTS playerStats ( id INTEGER NOT NULL PRIMARY KEY AUTO"+(plugin.useMYSQL?"_":"")+"INCREMENT UNIQUE, name TINYTEXT NOT NULL, dateJoined DATE NOT NULL, timeOnServer BIGINT UNSIGNED NOT NULL, lastLogin TIMESTAMP NOT NULL, numJoins INT UNSIGNED NOT NULL, numKicks INT UNSIGNED NOT NULL, blocksBroken INT UNSIGNED NOT NULL, blocksPlaced INT UNSIGNED NOT NULL, playersKilled INT UNSIGNED NOT NULL, deaths INT UNSIGNED NOT NULL, modRequests INT UNSIGNED NOT NULL);");
			preparedStatement.executeUpdate();
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
		}
		finally {
			close(plugin);
		}
	}
	
	public LinkedList<Note> getNotes(String player) {
		// begin storing the results
		LinkedList<Note> notes = new LinkedList<Note>();
		
		try {
			// load the database
			connect();
			
			// get the results
			statement = connect.createStatement();
			if(!player.equals("*")) resultSet = statement.executeQuery("select * from notes where lower(notee)='"+player.toLowerCase()+"' order by date desc");
			else resultSet = statement.executeQuery("select * from notes order by date desc");
			
			// now go through the results..
			while(resultSet.next()) {
				Note note = new Note();
				note.id = resultSet.getInt("id");
				note.noteDate = resultSet.getDate("date");
				note.noteTaker = resultSet.getString("noteTaker");
				note.notee = resultSet.getString("notee");
				note.note = resultSet.getString("note");
				notes.add(note);
			}
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
		}
		finally {
			close(plugin);
		}
		return notes;
	}
	
	public boolean writeNote(String noteTaker, String notee, String note) {
		boolean ret = true;
		try {
			// load the database
			connect();
			
			// write the statement
			preparedStatement = connect.prepareStatement("insert into notes (id, date, noteTaker, notee, note) values (NULL, ?, ?, ?, ?)");
			preparedStatement.setDate(1, new java.sql.Date((new java.util.Date()).getTime()));
			preparedStatement.setString(2, noteTaker);
			preparedStatement.setString(3, notee);
			preparedStatement.setString(4, note);
			
			// and execute it!
			preparedStatement.executeUpdate();
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
			ret = false;
		}
		finally {
			close(plugin);
		}
		return ret;
	}
	
	public boolean deleteNote(Integer noteID) {
		boolean ret = true;
		try {
			// load the database
			connect();
			
			// write the statement
			preparedStatement = connect.prepareStatement("delete from notes where id=?");
			preparedStatement.setInt(1, noteID);
			preparedStatement.executeUpdate();
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
			ret = false;
		}
		finally {
			close(plugin);
		}
		return ret;
	}
	
	public Stat getStat(String player) {
		// begin storing the results
		Stat stat = new Stat();
		stat.name = player;
		stat.dateJoined = new Date();
		
		try {
			// load the database
			connect();
			
			// get the results
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from playerStats where lower(name)='"+player.toLowerCase()+"' limit 1");
			
			// now go through the results..
			if(resultSet.next()) {
				stat.name = resultSet.getString("name");
				stat.dateJoined = resultSet.getDate("dateJoined");
				stat.timeOnServer = resultSet.getLong("timeOnServer");
				try {
					stat.lastLogin = new Date(resultSet.getTimestamp("lastLogin").getTime());
				}
				catch(Exception e) {
					stat.lastLogin = new Date();
				}
				stat.numJoins = resultSet.getInt("numJoins");
				stat.numKicks = resultSet.getInt("numKicks");
				stat.blocksBroken = resultSet.getInt("blocksBroken");
				stat.blocksPlaced = resultSet.getInt("blocksPlaced");
				stat.playersKilled = resultSet.getInt("playersKilled");
				stat.deaths = resultSet.getInt("deaths");
				stat.modRequests = resultSet.getInt("modRequests");
			}
			else {
				//stat = null;
				plugin.log.info("[PlayerNotes] Retrieved no or invalid database entry for "+player+"!");
			}
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
		}
		finally {
			close(plugin);
		}
		return stat;
	}
	
	public boolean setStat(String player, Stat stat) {
		boolean ret = true;
		try {
			// load the database
			connect();
			
			// write the statement
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from playerStats where lower(name)='"+player.toLowerCase()+"'");
			
			if(resultSet.next()) {
				// they already exist, update the record
				// but first, get their id!
				int id = resultSet.getInt("id");
				
				// now write the statement..
				preparedStatement = connect.prepareStatement("update playerStats set name=?, dateJoined=?, timeOnServer=?, lastLogin=?, numJoins=?, numKicks=?, blocksBroken=?, blocksPlaced=?, playersKilled=?, deaths=?, modRequests=? where id="+id+";");
				preparedStatement.setString(1, stat.name);
				preparedStatement.setDate(2, new java.sql.Date(stat.dateJoined.getTime()));
				preparedStatement.setLong(3, stat.timeOnServer);
				preparedStatement.setDate(4, new java.sql.Date(stat.lastLogin.getTime()));
				preparedStatement.setInt(5, stat.numJoins);
				preparedStatement.setInt(6, stat.numKicks);
				preparedStatement.setInt(7, stat.blocksBroken);
				preparedStatement.setInt(8, stat.blocksPlaced);
				preparedStatement.setInt(9, stat.playersKilled);
				preparedStatement.setInt(10, stat.deaths);
				preparedStatement.setInt(11, stat.modRequests);
				
				// and execute it!
				preparedStatement.executeUpdate();
			}
			else {
				// they don't exist yet, insert the record
				// write the statement
				preparedStatement = connect.prepareStatement("insert into playerStats (id, name, dateJoined, timeOnServer, lastLogin, numJoins, numKicks, blocksBroken, blocksPlaced, playersKilled, deaths, modRequests) values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				preparedStatement.setString(1, stat.name);
				preparedStatement.setDate(2, new java.sql.Date(stat.dateJoined.getTime()));
				preparedStatement.setLong(3, stat.timeOnServer);
				preparedStatement.setDate(4, new java.sql.Date(stat.lastLogin.getTime()));
				preparedStatement.setInt(5, stat.numJoins);
				preparedStatement.setInt(6, stat.numKicks);
				preparedStatement.setInt(7, stat.blocksBroken);
				preparedStatement.setInt(8, stat.blocksPlaced);
				preparedStatement.setInt(9, stat.playersKilled);
				preparedStatement.setInt(10, stat.deaths);
				preparedStatement.setInt(11, stat.modRequests);
				
				// and execute it!
				preparedStatement.executeUpdate();
			}
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
			ret = false;
		}
		finally {
			close(plugin);
		}
		return ret;
	}
	
	private void close(PlayerNotes plugin) {
		try {
			if(resultSet != null) resultSet.close();
			if(statement != null) statement.close();
			if(preparedStatement != null) preparedStatement.close();
			if(connect != null) connect.close();
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
		}
	}
}
