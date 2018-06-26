package fr.keke142.botlogger.managers;

import fr.keke142.botlogger.BotLoggerPlugin;
import fr.keke142.botlogger.configs.DatabaseConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private BotLoggerPlugin plugin;
    private DatabaseConfig database = new DatabaseConfig();

    private File file;
    private FileConfiguration config;

    public ConfigManager(BotLoggerPlugin plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "config.yml");
        plugin.createDefaultConfiguration(file, "config.yml");

        config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveDefaultConfig();

        database.load(config);
    }

    public void save() {
        try {
            database.save(config);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveDefaultConfig();

        database.load(config);
    }

    public DatabaseConfig getDatabaseConfig() {
        return database;
    }
}
