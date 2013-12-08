package uk.thecodingbadgers.tokens.database;

import uk.thecodingbadgers.bDatabaseManager.DatabaseTable.DatabaseTableData;

public class TokenUserData extends DatabaseTableData {
	
	public String playerName;
	public int tokenCount;
	public int totalTokenCount;
	
	/**
	 * Add tokens to user data
	 * @param tokenCount The number of tokens to award the player
	 */
	public void addTokens(int tokenCount) {
		tokenCount += tokenCount;
		totalTokenCount += tokenCount;
	}

}
