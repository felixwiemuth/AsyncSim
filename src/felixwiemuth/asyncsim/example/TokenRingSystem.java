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

import felixwiemuth.asyncsim.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Felix Wiemuth
 */
public class TokenRingSystem {

    public static void main(String[] args) {
        int n = 5;

        Simulator simulator = new Simulator(new StdOutLog());
        Network network = new Network(simulator);
        List<Task> tasks = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            Task t = new TokenRingTask(i, 0, simulator, network, 10);
            network.addNode(t);
            tasks.add(t);
        }
        Topology.addRing(network, 1);
        LoadGeneratorTask loadGenerator = new LoadGeneratorTask(0, simulator, network, 0);
        network.addNode(loadGenerator);
        tasks.add(loadGenerator);

        // Connect load generator to all other nodes
        for (int i = 1; i <= n; i++) {
            network.addLink(0, i, new FixedDuration(0));
        }

        // Connect load generator to itself, where the transport duration equals the interval between two laods being sent to the other nodes
        network.addLink(0, 0, new FixedDuration(25));

        network.initTasks();
        tasks.get(n).addMsg(new Message(0, 1, "genLoad"));
        tasks.get(n).schedule();

        simulator.step(50);
    }
}
