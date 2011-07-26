package com.hamaluik.PlayerNotes.commands;

import java.util.LinkedList;

import org.bukkit.command.CommandSender;

import com.hamaluik.PlayerNotes.Note;
import com.hamaluik.PlayerNotes.PlayerNotes;
import com.hamaluik.PlayerNotes.PlayerNotesCommandExecutor;

public class CommandNotes implements Command {
	private PlayerNotes plugin;
	public CommandNotes(PlayerNotes instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, String[] args) {
		// get the page
		Integer page = 0;
		String typedTarget = new String("");
		String target = new String("");
		if(args.length == 1) {
			page = 1;
			typedTarget = args[0];
		}
		else if(args.length == 2) {
			try {
				page = Integer.parseInt(args[1]);
				typedTarget = args[0];
				if(page < 1) throw new NumberFormatException();
			}
			catch(Exception e) {
				return false;
			}
		}
		else {
			return false;
		}
		
		// ok, we have the page and target
		// look up the target's actual name
		if(!typedTarget.equals("*")) {
			if(plugin.getServer().getPlayer(typedTarget) != null) {
				// the player is online, set their name
				target = plugin.getServer().getPlayer(typedTarget).getName();
			}
			else {
				target = typedTarget;
			}
		}
		else target = "*";
		
		// now call the query
		LinkedList<Note> notes = plugin.dbm.getNotes(target);

		// make sure we actually have notes about that player
		if(notes.size() > 0) {
			// and get the number of pages
			Integer numPages = notes.size() / 5;		
			if(notes.size() % 5 != 0) {
				numPages++;
			}
			
			// make sure we have a valid page
			if(page > numPages || page < 1) {
				page = 1;
			}
			
			// get the start and end indices
			Integer start = (page-1)*5;
			Integer end = start+5;
			if(end > notes.size()) {
				end = notes.size();
			}
			
			if(!target.equals("*")) {
				PlayerNotesCommandExecutor.returnMessage(sender, "&6Notes for &f" + target + " &7(Page "+page+"/"+numPages+")&6:");
				for(int i = start; i < end; i++) {
					Note note = notes.get(i);
					PlayerNotesCommandExecutor.returnMessage(sender, "&7[" + note.noteDate + "] &6" + note.noteTaker + ": &f" + note.note + " &8(" + note.id + ")");
				}
			}
			else {
				PlayerNotesCommandExecutor.returnMessage(sender, "&6All notes &7(Page "+page+"/"+numPages+")&6:");
				for(int i = start; i < end; i++) {
					Note note = notes.get(i);
					PlayerNotesCommandExecutor.returnMessage(sender, "&7[" + note.noteDate + "] &6" + note.noteTaker + ": &e"+note.notee+": &f" + note.note + " &8(" + note.id + ")");
				}
			}
		}
		else {
			PlayerNotesCommandExecutor.returnMessage(sender, "&6There are no notes on &f" + target + "&6!");
		}
		
		return true;
	}
	
	public String requiredPermission() {
		return "playernotes.notes";
	}
	
	public String getCommand() {
		return "notes";
	}
	
	public String getArguments() {
		return "<player> [page]";
	}
	
	public String getDescription() {
		return "get the notes for <player> on [page]";
	}
}
