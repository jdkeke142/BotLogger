package fr.keke142.botlogger.database;

import fr.keke142.botlogger.managers.DatabaseManager;
import fr.keke142.botlogger.objects.PlayerIp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotLogging implements IRepository {

    private DatabaseManager databaseManager;

    public BotLogging(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void addIp(String ip, Date date, int connections) {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

        PreparedStatement statment = connectionHandler.getPreparedStatement("insertIp");

        try {
            statment.setString(1, ip);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            statment.setString(2, dateFormat.format(date));
            statment.setDouble(3, connections);
            statment.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }
    }

    public PlayerIp getIp(String ip) {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

        PlayerIp playerIp = null;

        try {
            PreparedStatement statment = connectionHandler.getPreparedStatement("getIp");
            statment.setString(1, ip);

            ResultSet res = statment.executeQuery();
            while (res.next()) {
                playerIp = new PlayerIp(res.getString("ip"), res.getDate("date"), res.getInt("connections"));
            }
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }
        return playerIp;
    }

    public void incrementIpConnections(String ip, int connections) {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

        PreparedStatement statment = connectionHandler.getPreparedStatement("updateConnections");

        try {
            statment.setInt(1, connections);
            statment.setString(2, ip);
            statment.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }
    }

    @Override
    public String[] getTable() {
        return new String[]{"ips", "`id` INT NOT NULL AUTO_INCREMENT, " +
                "`ip` VARCHAR(32) NOT NULL," +
                "`date` datetime NOT NULL," +
                "`connections` INT NOT NULL," +
                "PRIMARY KEY (`id`)",
                "ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci"};
    }

    @Override
    public void registerPreparedStatements(ConnectionHandler connection) {
        connection.addPreparedStatement("getIp", "SELECT * FROM ips WHERE ip=?");
        connection.addPreparedStatement("insertIp", "INSERT INTO ips (ip, date, connections) VALUES(?, ?, ?)");
        connection.addPreparedStatement("updateConnections", "UPDATE ips SET connections=? WHERE ip=?");
    }
}
