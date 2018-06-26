package fr.keke142.botlogger.listeners;

import fr.keke142.botlogger.BotLoggerPlugin;
import fr.keke142.botlogger.managers.database.BotLoggingManager;
import fr.keke142.botlogger.objects.PlayerIp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Date;

public class BotLoggingListener implements Listener {

    private BotLoggerPlugin botLoggerPlugin;

    public BotLoggingListener(BotLoggerPlugin plugin) {
        this.botLoggerPlugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        BotLoggingManager botLoggingManager = botLoggerPlugin.getBotLoggingManager();

        String playerAdress = player.getAddress().getHostString();

        PlayerIp databaseIp = botLoggingManager.getIp(playerAdress);

        if (databaseIp != null) {
            botLoggingManager.incrementIpConnections(playerAdress, databaseIp.getConnections() + 1);
            botLoggerPlugin.getLogger().info(playerAdress + " incremented in database, this is the " + (databaseIp.getConnections() + 1) + " time I seen this Ip adress.");
            player.kickPlayer("Bot kick");
            return;

        }

        Date dateNow = new Date();
        botLoggingManager.addIp(playerAdress, dateNow, 1);

        botLoggerPlugin.getLogger().info(playerAdress + " added to the database, this is the first time I seen this Ip adress.");
        player.kickPlayer("Bot kick");
    }
}
