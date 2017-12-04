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
import felixwiemuth.asyncsim.Link;
import felixwiemuth.asyncsim.Network;
import felixwiemuth.asyncsim.Simulator;
import felixwiemuth.asyncsim.StdOutLog;

/**
 *
 * @author Felix Wiemuth
 */
public class BerkeleySystem {

    public void run(int n, int steps) {
        int serverId = 0;

        Simulator simulator = new Simulator(new StdOutLog());
        simulator.setLogMsgSent(true);
        Network network = new Network(simulator);

        // NOTE: To simulate jitter, can add a random duration here
        Link defaultLink = new Link(new FixedDuration(0));

        BerkeleyServer server = new BerkeleyServer(serverId, simulator, network);
        network.addNode(server);

        for (int i = 1; i <= n; i++) {
            network.addNode(new BerkeleyClient(i, simulator, network));
            network.addTwoWayLink(serverId, i, defaultLink);
        }

        network.initTasks();

        simulator.step(steps);
    }

    public static void main(String[] args) {
        new BerkeleySystem().run(5, 1000);
    }

}
