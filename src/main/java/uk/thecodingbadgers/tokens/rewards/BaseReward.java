package uk.thecodingbadgers.tokens.rewards;

import org.bukkit.event.Listener;

public abstract class BaseReward implements Listener  {
	
	protected boolean enabled = false;
	
	public abstract boolean Load();

}
