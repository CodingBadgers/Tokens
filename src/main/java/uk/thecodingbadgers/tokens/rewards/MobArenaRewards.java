package uk.thecodingbadgers.tokens.rewards;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import uk.thecodingbadgers.tokens.Tokens;
import uk.thecodingbadgers.tokens.database.TokenUserData;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.ArenaCompleteEvent;
import com.garbagemule.MobArena.framework.Arena;

public class MobArenaRewards implements Listener {
	
	private Map<String, Integer> arenaCompleteRewards;
	
	private boolean enabled = true;
	
	/**
	 * Class constructor
	 */
	public MobArenaRewards() {
		this.arenaCompleteRewards = new HashMap<String, Integer>();
		createConfigs();
	}
	
	/**
	 * Create/Load configs
	 */
	private void createConfigs() {
		
		Tokens tokens = Tokens.getInstance();
		MobArena MobArenaPlugin = (MobArena)tokens.getServer().getPluginManager().getPlugin("MobArena");
		if (MobArenaPlugin == null) {
			this.enabled = false;
			return;
		}
		
		File mobArenaFile = new File(tokens.getDataFolder() + File.separator + "mobArena.yml");
		List<Arena> arenas = MobArenaPlugin.getArenaMaster().getArenas();
		
		// Create a new config
		if (!mobArenaFile.exists()) {
			try {
				mobArenaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(mobArenaFile);
		
		for (Arena arena : arenas) {
			final String arenaPath = arena.arenaName() + ".completeReward";
			
			if (!config.contains(arenaPath)) {
				config.set(arenaPath, 1);
			}
			
			final int completeReward = config.getInt(arenaPath);
			this.arenaCompleteRewards.put(arena.arenaName(), completeReward);
		}
		
		try {
			config.save(mobArenaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handle players completing the final wave in a mob arena
	 * 
	 * @param event The arena complete event
	 */
	@EventHandler
	public void onMobArenaComplete(ArenaCompleteEvent event) {
		
		if (!enabled) {
			return;
		}
		
		final String arenaName = event.getArena().arenaName();
		int rewardTokens = 1;
		
		if (this.arenaCompleteRewards.containsKey(arenaName)) {
			rewardTokens = this.arenaCompleteRewards.get(arenaName);
		}
		
		Tokens tokens = Tokens.getInstance();
		for (Player player : event.getSurvivors()) {
			TokenUserData data = tokens.getUsers().get(player.getName());
			data.addTokens(rewardTokens);
			tokens.output(player, "You have been awarded " + ChatColor.GOLD + rewardTokens + " tokens!");
		}
		
	}

}
