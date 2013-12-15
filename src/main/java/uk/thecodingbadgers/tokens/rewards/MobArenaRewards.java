package uk.thecodingbadgers.tokens.rewards;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import uk.thecodingbadgers.tokens.Tokens;
import uk.thecodingbadgers.tokens.database.TokenUserData;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.ArenaCompleteEvent;
import com.garbagemule.MobArena.events.ArenaPlayerDeathEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import com.garbagemule.MobArena.framework.Arena;

public class MobArenaRewards extends BaseReward {
	
	private Map<String, Integer> arenaCompleteRewards;
	private Map<String, Integer> arena10KillRewards;
	
	private Map<String, Integer> playerMobKills;
	
	private MobArena mobArenaPlugin = null;
	
	/**
	 * Class constructor
	 */
	public MobArenaRewards() {
		this.arenaCompleteRewards = new HashMap<String, Integer>();
		this.arena10KillRewards = new HashMap<String, Integer>();
		this.playerMobKills = new HashMap<String, Integer>();
		
		Tokens tokens = Tokens.getInstance();
		this.mobArenaPlugin = (MobArena)tokens.getServer().getPluginManager().getPlugin("MobArena");
		if (this.mobArenaPlugin == null) {
			return;
		}
		
		this.enabled = true;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean Load() {
		
		if (this.enabled == false)
			return false;
		
		createConfigs();
		return true;
	}
	
	/**
	 * Create/Load configs
	 */
	private void createConfigs() {
		
		Tokens tokens = Tokens.getInstance();
		
		File mobArenaFile = new File(tokens.getDataFolder() + File.separator + "rewards" + File.separator + "mobArena.yml");
		List<Arena> arenas = this.mobArenaPlugin.getArenaMaster().getArenas();
		
		mobArenaFile.getParentFile().mkdirs();		
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
			
			// Complete rewards
			final String completeRewardPath = arena.arenaName() + ".completeReward";
			
			if (!config.contains(completeRewardPath)) {
				config.set(completeRewardPath, 1);
			}
			
			final int completeReward = config.getInt(completeRewardPath);
			this.arenaCompleteRewards.put(arena.arenaName(), completeReward);
			
			// 10 kills rewards
			final String Kills10RewardPath = arena.arenaName() + ".kill10Mobs";
			
			if (!config.contains(Kills10RewardPath)) {
				config.set(Kills10RewardPath, 2);
			}
			
			final int kills10Reward = config.getInt(Kills10RewardPath);
			this.arena10KillRewards.put(arena.arenaName(), kills10Reward);
			
		}
		
		try {
			config.save(mobArenaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerLeave(ArenaPlayerLeaveEvent event) {
		if (this.arena10KillRewards.containsKey(event.getPlayer().getName()))
			this.arena10KillRewards.remove(event.getPlayer().getName());
	}
	
	/**
	 * 
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerDeath(ArenaPlayerDeathEvent event) {
		if (this.arena10KillRewards.containsKey(event.getPlayer().getName()))
			this.arena10KillRewards.remove(event.getPlayer().getName());
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
			tokens.getDatabaseManager().saveUser(data, false);
			tokens.output(player, "You have been awarded " + ChatColor.YELLOW + rewardTokens + " tokens " + ChatColor.GOLD + "for completing " + arenaName + "!");
		}
		
	}
	
	/**
	 * Arena start event, clear out any old 10 kill stats
	 * @param event
	 */
	public void onArenaStart(ArenaStartEvent event) {
		
		Arena arena = event.getArena();
		
		for (Player player : arena.getAllPlayers())
		{
			if (this.arena10KillRewards.containsKey(player.getName()))
				this.arena10KillRewards.remove(player.getName());
		}
		
	}
	
	/**
	 * Handle players completing the final wave in a mob arena
	 * 
	 * @param event The arena complete event
	 */
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		
		LivingEntity entity = event.getEntity();
		
		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;
		
		EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent)entity.getLastDamageCause();
		Player player = null;
		if (damageEvent.getDamager() instanceof Player) {
			player = (Player)damageEvent.getDamager();
		}
		else if (damageEvent.getDamager() instanceof Projectile) {
			player = (Player)((Projectile)damageEvent.getDamager()).getShooter();
		}

		Tokens tokens = Tokens.getInstance();
		List<Arena> arenas = this.mobArenaPlugin.getArenaMaster().getArenas();
		for (Arena arena : arenas) {
			if (arena.inArena(player)) {
				
				if (!this.playerMobKills.containsKey(player.getName())) {
					this.playerMobKills.put(player.getName(), 1);
					return;
				}
				
				final int noofKills = this.playerMobKills.get(player.getName()) + 1;
				this.playerMobKills.put(player.getName(), noofKills);
				
				if (noofKills % 10 == 0)
				{
					TokenUserData data = tokens.getUsers().get(player.getName());
					final int rewardTokens = this.arena10KillRewards.get(arena.arenaName());
					data.addTokens(rewardTokens);
					tokens.getDatabaseManager().saveUser(data, false);
					tokens.output(player, "You have been awarded " + ChatColor.YELLOW + rewardTokens + " tokens " + ChatColor.GOLD + "for killing " + noofKills + " mobs!");
				}
				
				return;
			}
		}
		
	}
	
}
