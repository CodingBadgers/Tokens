package uk.thecodingbadgers.tokens.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import uk.thecodingbadgers.bDatabaseManager.bDatabaseManager;
import uk.thecodingbadgers.bDatabaseManager.bDatabaseManager.DatabaseType;
import uk.thecodingbadgers.bDatabaseManager.Database.BukkitDatabase;
import uk.thecodingbadgers.bDatabaseManager.DatabaseTable.DatabaseTable;
import uk.thecodingbadgers.tokens.Tokens;

public class DatabaseManager {
	
	/**
	 * The database instance
	 */
	BukkitDatabase m_database;
	
	/**
	 * The users table
	 */
	private DatabaseTable m_usersTable;
	
	/**
	 * Class constructor
	 */
	public DatabaseManager() {
		setupDatabase();
	}

	/**
	 * Setup the database and its tables
	 */
	private void setupDatabase() {
		m_database = bDatabaseManager.createDatabase("Tokens", Tokens.getInstance(), DatabaseType.SQLite);
		if (m_database == null) {
			Tokens.getInstance().getLogger().log(Level.SEVERE, "Failed to setup database!");
			return;
		}
		
		m_usersTable = m_database.createTable("Users", TokenUserData.class);
		if (m_usersTable == null) {
			Tokens.getInstance().getLogger().log(Level.SEVERE, "Failed to setup users table!");
			return;
		}
	}

	/**
	 * Save a users token data to the database
	 * @param data The data to save
	 * @param instant Should we force the data to be save instantally, or add it to the save thread
	 */
	public void saveUser(TokenUserData data, boolean instant) {
		
		ResultSet result = m_database.queryResult("SELECT * FROM Users WHERE playerName='" + data.playerName + "'");
		try {
			if (result == null || !result.next()) {
				m_usersTable.insert(data, TokenUserData.class, instant);
				return;
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		m_usersTable.update(data, TokenUserData.class, "playerName='" + data.playerName + "'", instant);
	}

	/**
	 * Load a user from the database and add them to the user map
	 * @param playerName The name of the user to load
	 * @return True if they were loaded, false if not
	 */
	public boolean loadUser(String playerName) {
		
		ResultSet result = m_database.queryResult("SELECT * FROM Users WHERE playerName='" + playerName + "'");
		try {
			if (result == null) {
				return false;
			}
			
			if (result.next()) {
				
				 TokenUserData data = new TokenUserData();
				 data.playerName = result.getString("playerName");
				 data.tokenCount = result.getInt("tokenCount");
				 data.totalTokenCount = result.getInt("totalTokenCount");
				 
				 Tokens.getInstance().getUsers().put(data.playerName, data);
				 result.close();
				 
				 return true;
			 }
			 else {
				 result.close();
				 return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
