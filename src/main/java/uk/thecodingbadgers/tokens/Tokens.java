package uk.thecodingbadgers.tokens;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.thecodingbadgers.tokens.database.DatabaseManager;
import uk.thecodingbadgers.tokens.database.TokenUserData;
import uk.thecodingbadgers.tokens.listeners.TokenUserListener;

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
	
}
