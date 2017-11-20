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

package felixwiemuth.asyncsim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Felix Wiemuth
 */
public class Network {

    private final Simulator simulator;

    Map<Integer, Task> tasks = new HashMap<>();
    // ID (source) -> (ID (dest) -> Duration (link delay))
    Map<Integer, Map<Integer, Duration>> connections = new HashMap<>();

    public Network(Simulator simulator) {
        this.simulator = simulator;
    }

    public void addNode(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addLink(int src, int dest, Duration duration) {
        Map<Integer, Duration> map = connections.get(src);
        if (map == null) {
            map = new HashMap<>();
            connections.put(src, map);
        }
        map.put(dest, duration);
    }

    public Set<Integer> getNeighbors(int src) {
        Set<Integer> neighbors = connections.get(src).keySet();
        if (neighbors == null) {
            neighbors = new HashSet<>();
        }
        return neighbors;
    }

    public int size() {
        return tasks.size();
    }

    public void sendMsg(final Message msg) {
        if (connections.get(msg.getDest()) != null && connections.get(msg.getSrc()).containsKey(msg.getDest())) { // check whether link exists
            simulator.addEvent(connections.get(msg.getSrc()).get(msg.getDest()).getDuration(), new Runnable() {
                @Override
                public void run() {
                    tasks.get(msg.getDest()).addMsg(msg);
                }
            });
        }
    }

    public void initTasks() {
        for (Task task : tasks.values()) {
            task.onInit();
        }
    }
}
