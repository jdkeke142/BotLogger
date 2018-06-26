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

package fr.keke142.botlogger.managers.database;

import fr.keke142.botlogger.BotLoggerPlugin;
import fr.keke142.botlogger.managers.DatabaseManager;
import fr.keke142.botlogger.objects.PlayerIp;

import java.util.Date;

public class BotLoggingManager {

    private DatabaseManager databaseManager;

    public BotLoggingManager(BotLoggerPlugin plugin) {
        databaseManager = plugin.getDatabaseManager();
    }

    public void addIp(String ip, Date date, int connections) {
        databaseManager.getBotLogging().addIp(ip, date, connections);
    }

    public PlayerIp getIp(String ip) {
        return databaseManager.getBotLogging().getIp(ip);
    }

    public void incrementIpConnections(String ip, int connections) {
        databaseManager.getBotLogging().incrementIpConnections(ip, connections);
    }
}
