package fr.keke142.botlogger.database;

import fr.keke142.botlogger.BotLoggerPlugin;
import fr.keke142.botlogger.managers.ConfigManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ConnectionPool {
    private BotLoggerPlugin plugin;
    private ConfigManager configManager;
    private ArrayList<IRepository> repositories = new ArrayList<>();
    private ArrayList<ConnectionHandler> connections = new ArrayList<>();

    public ConnectionPool(BotLoggerPlugin plugin) {
        this.plugin = plugin;
        configManager = plugin.getConfigManager();
    }

    public void addRepository(IRepository repository) {
        repositories.add(repository);
    }

    public void initializeConnections() {
        for (int i = 0; i < configManager.getDatabaseConfig().getThreads(); i++) {
            ConnectionHandler ch = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://" + configManager.getDatabaseConfig().getHost() + ":" + configManager.getDatabaseConfig().getPort() + "/" + configManager.getDatabaseConfig().getDatabase() + "?useSSL=false&useUnicode=true&character_set_server=utf8mb4", configManager.getDatabaseConfig().getUserName(), configManager.getDatabaseConfig().getPassword());
                ch = new ConnectionHandler(connection);
                for (IRepository repository : repositories) {
                    repository.registerPreparedStatements(ch);
                }
            } catch (SQLException | ClassNotFoundException e) {
                plugin.getLogger().severe("SQL is unable to connect!");
                e.printStackTrace();
            }

            if (ch != null) {
                connections.add(ch);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<ConnectionHandler> cons = connections.iterator();
                while (cons.hasNext()) {
                    ConnectionHandler con = cons.next();

                    if (!con.isUsed() && con.isOldConnection()) {
                        con.closeConnection();
                        cons.remove();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 10 * 20, 10 * 20);

        plugin.getLogger().info("Creating SQL tables if not exists.");
        for (IRepository repository : repositories) {
            String[] tableInformation = repository.getTable();

            if (!doesTableExist(tableInformation[0])) {
                try {
                    standardQuery("CREATE TABLE IF NOT EXISTS `" + tableInformation[0] + "` (" + tableInformation[1] + ") " + tableInformation[2] + ";");
                } catch (SQLException e) {
                    e.printStackTrace();
                    plugin.getLogger().severe("Could not create Table!");
                }
            }
        }

        configManager.save();
    }

    public synchronized ConnectionHandler getConnection() {
        for (ConnectionHandler c : connections) {
            if (!c.isUsed()) {
                return c;
            }
        }

        ConnectionHandler ch;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + configManager.getDatabaseConfig().getHost() + ":" + configManager.getDatabaseConfig().getPort() + "/" + configManager.getDatabaseConfig().getDatabase() + "?useSSL=false&useUnicode=true&character_set_server=utf8mb4", configManager.getDatabaseConfig().getUserName(), configManager.getDatabaseConfig().getPassword());

            ch = new ConnectionHandler(connection);
            for (IRepository repository : repositories) {
                repository.registerPreparedStatements(ch);
            }
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("SQL is unable to connect!");
            return null;
        }

        connections.add(ch);

        plugin.getLogger().info("Created new sql connection!");

        return ch;

    }

    private void standardQuery(String query) throws SQLException {
        ConnectionHandler ch = getConnection();

        Statement statement = ch.getConnection().createStatement();
        statement.executeUpdate(query);
        statement.close();

        ch.release();
    }

    private boolean doesTableExist(String table) {
        ConnectionHandler ch = getConnection();
        boolean check = checkTable(table, ch.getConnection());
        ch.release();

        return check;
    }

    private boolean checkTable(String table, Connection connection) {
        DatabaseMetaData dbm = null;
        try {
            dbm = connection.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ResultSet tables = null;
        try {
            tables = Objects.requireNonNull(dbm).getTables(null, null, table, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean check = false;
        try {
            check = Objects.requireNonNull(tables).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return check;
    }

    public void closeConnections() {
        for (ConnectionHandler c : connections) {
            c.closeConnection();
        }
    }
}

