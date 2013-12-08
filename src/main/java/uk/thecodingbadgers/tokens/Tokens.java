package uk.thecodingbadgers.tokens;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheCodingBadgers
 * 
 *         Main entry class for the Tokens plugin
 * 
 */
public final class Tokens extends JavaPlugin {

	/** The instance of the Tokens plugin */
	private static Tokens instance = null;

	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable() {

		// Store the instance of the plugin
		Tokens.instance = this;

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
	public static Tokens getInstace() {
		return Tokens.instance;
	}
	
}
