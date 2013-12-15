package uk.thecodingbadgers.tokens;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.thecodingbadgers.tokens.database.DatabaseManager;
import uk.thecodingbadgers.tokens.database.TokenUserData;
import uk.thecodingbadgers.tokens.listeners.TokenUserListener;
import uk.thecodingbadgers.tokens.rewards.BaseReward;
import uk.thecodingbadgers.tokens.rewards.MobArenaRewards;

/**
 * @author TheCodingBadgers
 * 
 *         Main entry class for the Tokens plugin
 * 
 */
public final class Tokens extends JavaPlugin {

	/** The instance of the Tokens plugin */
	private static Tokens instance = null;
	
	/** The database manager instance */
	private DatabaseManager databaseManager;
	
	/** A map of player names and their user data */
	Map<String, TokenUserData> users;

	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable() {

		// Store the instance of the plugin
		Tokens.instance = this;
		
		// Allocated managers
		this.databaseManager = new DatabaseManager();
		this.users = new HashMap<String, TokenUserData>();
		
		// Register listeners
		PluginManager pluginManager = this.getServer().getPluginManager();
		pluginManager.registerEvents(new TokenUserListener(), this);
		
		
		BaseReward maRewards = new MobArenaRewards();
		if (maRewards.Load()) {
			pluginManager.registerEvents(maRewards, this);
		}
		
		for (Player player : this.getServer().getOnlinePlayers())
		{
			this.loadUser(player.getName());
		}
	}

	/**
	 * Called when the plugin is disabled
	 */
	public void onDisable() {
		// Reset the instance on disable
		Tokens.instance = null;
	}
	
	/**
	 * Access to the singleton class instance
	 * @return The instance of the Tokens plugin
	 */
	public static Tokens getInstance() {
		return Tokens.instance;
	}
	
	/**
	 * Access to the plugins database manager
	 * @return The instance of the database manager
	 */
	public DatabaseManager getDatabaseManager() {
		return this.databaseManager;
	}
	
	/**
	 * Get the user map
	 * @return A hash map containing the user data, where the key is a players name
	 */
	public Map<String, TokenUserData> getUsers() {
		return this.users;
	}

	/** 
	 * Output a message to a given command sender
	 * @param destination The thing to output the message to 
	 * @param message The message to output
	 */
	public void output(CommandSender destination, String message) {
		destination.sendMessage(ChatColor.GOLD + message);
	}

	/**
	 * 
	 * @param playerName
	 */
	public void loadUser(String playerName) {
		
		// This player is already loaded, huzzah!
		if (this.getUsers().containsKey(playerName)) {
			return;
		}
		
		// This player already exists in the database, load them
		if (this.getDatabaseManager().loadUser(playerName)) {
			return;
		}
		
		// New player, create them some data
		final TokenUserData data = new TokenUserData();
		data.playerName = playerName;
		data.tokenCount = 0;
		data.totalTokenCount = 0;
		
		this.getUsers().put(playerName, data);
		this.getDatabaseManager().saveUser(data, true);
	}
	
	/**
	 * 
	 */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	
    	if (!cmd.getName().equalsIgnoreCase("tokens")) {
			return false;
		}
    	
    	if (args.length == 0) {
    		if (!(sender instanceof Player))
    			return true;
    		
    		Player player = (Player)sender;
    		TokenUserData data = this.getUsers().get(player.getName());
    		this.output(sender, "You currently have " + data.tokenCount + " tokens, and have collected " + data.totalTokenCount + " tokens in total.");
    		return true;
    	}
    	
    	return true; 
    }


	
}
