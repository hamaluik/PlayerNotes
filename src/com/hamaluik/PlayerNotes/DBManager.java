package com.hamaluik.PlayerNotes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
				connect = DriverManager.getConnection("jdbc:mysql://localhost/"+plugin.databaseName+"?"
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
	
	public void ensureTableExists() {
		try {
			// load the database
			connect();

			// create the table if it does not exist
			preparedStatement = connect.prepareStatement("CREATE TABLE IF NOT EXISTS notes ( id INTEGER NOT NULL PRIMARY KEY AUTO"+(plugin.useMYSQL?"_":"")+"INCREMENT UNIQUE, date DATETIME NOT NULL, noteTaker TINYTEXT NOT NULL, notee TINYTEXT NOT NULL, note MEDIUMTEXT NOT NULL );");
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
			resultSet = statement.executeQuery("select * from notes where notee='"+player+"' order by date desc");
			
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
	
	private void close(PlayerNotes plugin) {
		try {
			if(resultSet != null) resultSet.close();
			if(statement != null) statement.close();
			if(connect != null) connect.close();
		}
		catch(Exception e) {
			plugin.log.log(Level.SEVERE, "Database exception", e);
		}
	}
}
