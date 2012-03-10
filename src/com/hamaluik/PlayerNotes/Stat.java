package com.hamaluik.PlayerNotes;

import java.util.Date;

public class Stat {
	public boolean changed = false;
	public String name = new String("");
	public Date dateJoined = new Date();
	public long joinTime = -1; // for internal time-on-server keeping
	public long timeOnServer;
	public Date lastLogin = new Date();
	public int numJoins;
	public int numKicks;
	public int blocksBroken;
	public int blocksPlaced;
	public int playersKilled;
	public int deaths;
	public int modRequests;
}
