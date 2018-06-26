/*
 * Copyright 2018 Jimenez Kévin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.keke142.botlogger;

import fr.keke142.botlogger.listeners.BotLoggingListener;
import fr.keke142.botlogger.managers.ConfigManager;
import fr.keke142.botlogger.managers.DatabaseManager;
import fr.keke142.botlogger.managers.database.BotLoggingManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class BotLoggerPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private DatabaseManager databaseManager;

    private BotLoggingManager botLoggingManager;

    public void onEnable() {
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this);

        botLoggingManager = new BotLoggingManager(this);

        databaseManager.loadDatabase();
        Bukkit.getPluginManager().registerEvents(new BotLoggingListener(this), this);
    }

    public void onDisable() {
        databaseManager.getConnectionPool().closeConnections();
    }

    /**
     * Create a default configuration file from the .jar.
     *
     * @param actual The destination file
     * @param defaultName The name of the file inside the jar's defaults folder
     */
    public void createDefaultConfiguration(File actual, String defaultName) {
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (actual.exists()) {
            return;
        }

        JarFile file = null;
        InputStream input = null;
        try {
            file = new JarFile(getFile());
            ZipEntry copy = file.getEntry(defaultName);
            if (copy == null) {
                file.close();
                throw new FileNotFoundException();
            }
            input = file.getInputStream(copy);
        } catch (IOException e) {
            getLogger().severe("Unable to read default configuration: " + defaultName);
        }

        if (input != null) {
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length = 0;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                getLogger().info("Default configuration file written: " + actual.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException ignore) {
                }

                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ignore) {
                }
            }
        }
        if (file != null) {
            try {
                file.close();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * Get the bot database manager
     *
     * @return Plugin database manager
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Get the bot logging manager
     *
     * @return Plugin logging manager
     */
    public BotLoggingManager getBotLoggingManager() {
        return botLoggingManager;
    }

    /**
     * Get the plugin config manager
     *
     * @return Plugin config manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
