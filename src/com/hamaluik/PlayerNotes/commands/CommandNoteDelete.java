package com.hamaluik.PlayerNotes.commands;

import org.bukkit.command.CommandSender;

import com.hamaluik.PlayerNotes.PlayerNotes;
import com.hamaluik.PlayerNotes.PlayerNotesCommandExecutor;

public class CommandNoteDelete implements Command {
	private PlayerNotes plugin;
	public CommandNoteDelete(PlayerNotes instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, String[] args) {
		// make sure they're providing enough information
		if(args.length != 1) return false;
		
		// get the ID to delete
		Integer targetID;
		try {
			targetID = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
			return false;
		}
		
		// and delete it!
		if(!plugin.dbm.deleteNote(targetID)) {
			PlayerNotesCommandExecutor.returnMessage(sender, "&cAn error occurred, you probably used an invalid ID!");
		}
		else {
			PlayerNotesCommandExecutor.returnMessage(sender, "&6Your note has been deleted successfully!");
		}
		
		return true;
	}
	
	public String requiredPermission() {
		return "playernotes.notedelete";
	}
	
	public String getCommand() {
		return "nd";
	}
	
	public String getArguments() {
		return "<id>";
	}
	
	public String getDescription() {
		return "deletes a note with the id <id>";
	}

}
