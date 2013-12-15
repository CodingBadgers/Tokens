package uk.thecodingbadgers.tokens.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.thecodingbadgers.tokens.Tokens;
import uk.thecodingbadgers.tokens.database.TokenUserData;

public class TokenUserListener implements Listener {
	
	/**
	 * Handle players quitting the game
	 * 
	 * @param event The player quit event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		
		final Tokens plugin = Tokens.getInstance();
		if (!plugin.getUsers().containsKey(playerName)) {
			return;
		}
		
		final TokenUserData data = plugin.getUsers().get(playerName);
		plugin.getDatabaseManager().saveUser(data, true);
		
	}
	
	/**
	 * Handle players joining the game
	 * 
	 * @param event The player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		
		final Tokens plugin = Tokens.getInstance();
		plugin.loadUser(playerName);
		
	}

}
