package fr.keke142.botlogger.managers;

import fr.keke142.botlogger.BotLoggerPlugin;
import fr.keke142.botlogger.database.BotLogging;
import fr.keke142.botlogger.database.ConnectionPool;

public class DatabaseManager {

    private BotLoggerPlugin plugin;
    private ConnectionPool connectionPool;

    private BotLogging botLogging;

    public DatabaseManager(BotLoggerPlugin plugin)  {
        this.plugin = plugin;
    }

    public void loadDatabase() {
        botLogging = new BotLogging(this);

        connectionPool = new ConnectionPool(plugin);
        connectionPool.addRepository(botLogging);
        connectionPool.initializeConnections();
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public BotLogging getBotLogging() {
        return botLogging;
    }
}
