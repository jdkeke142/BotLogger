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

package fr.keke142.botlogger.objects;

import java.util.Date;

public class PlayerIp {

    private String ip;
    private Date date;
    private int connections;

    public PlayerIp(String ip, Date date, int connections) {
        this.ip = ip;
        this.date = date;
        this.connections = connections;
    }

    public String getIp() {
        return ip;
    }

    public Date getDate() {
        return date;
    }

    public int getConnections() {
        return connections;
    }
}
