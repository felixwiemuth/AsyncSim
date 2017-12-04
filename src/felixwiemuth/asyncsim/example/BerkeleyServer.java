/*
 * Copyright (C) 2017 Felix Wiemuth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package felixwiemuth.asyncsim.example;

import felixwiemuth.asyncsim.FixedDuration;
import felixwiemuth.asyncsim.Message;
import felixwiemuth.asyncsim.Network;
import felixwiemuth.asyncsim.RandomDuration;
import felixwiemuth.asyncsim.Simulator;
import felixwiemuth.asyncsim.Task;
import java.util.HashMap;
import java.util.Map;

/**
 * A time server following the Berkeley algorithm. It periodically initiates a
 * synchronization with all its neighbors (clients). If some clients don't reply
 * for a synchronization, they are ignored. It assumes to only receive time
 * reports.
 *
 * @author Felix Wiemuth
 */
public class BerkeleyServer extends Task {

    private Map<Integer, Integer> collectedTimes; // client -> local time

    /**
     * Initiates a synchronization by requesting the local times from all
     * clients.
     */
    private final Action initiateSync = new Action() {
        @Override
        public void run() {
            log("Beginning sync...");
            collectedTimes = new HashMap<>(); // reset collect times
            for (int client : getNeighbors()) {
                sendMsg(client, "getTime"); // request local time from 'client'
            }
            // After a fixed amount of time, stop collecting answers from clients
            scheduleAction(terminateSync, new FixedDuration(50));
        }
    };

    /**
     * Completes a synchronization by calculating the mean time and sending the
     * individual diffs to the clients.
     */
    private final Action terminateSync = new Action() {
        @Override
        public void run() {
            int sum = 0;
            for (int localTime : collectedTimes.values()) {
                sum += localTime;
            }
            int mean = sum / collectedTimes.values().size();
            log("Mean is " + mean);
            for (int client : getNeighbors()) {
                sendMsg(client, mean - collectedTimes.get(client)); // request local times from clients
            }
            log("Sync completed.");
            // Schedule next sync
            scheduleAction(initiateSync, new RandomDuration(getRandom(), 1000, 500));
        }
    };

    public BerkeleyServer(int id, Simulator simulator, Network network) {
        super(id, simulator, network);

        // Every message received is interpreted as a time report from a client.
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return hasMsg();
            }
        }, new Action() {
            @Override
            public void run() {
                Message msg = pollMsg();
                collectedTimes.put(msg.getSrc(), (Integer) msg.getData());
            }
        }));
    }

    @Override
    protected void onInit() {
        scheduleAction(initiateSync, new FixedDuration(0));
    }
}
