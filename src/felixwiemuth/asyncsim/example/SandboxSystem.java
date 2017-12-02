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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Models the sandbox problem.
 *
 * @author Felix Wiemuth
 */
public class SandboxSystem {

    /**
     * Solves the sandbox problem. Uses the distributed system composed of a
     * {@link SandboxParent}, a {@link Sandbox} and {@code n}
     * {@link SandboxChild}ren.
     *
     * @param n number of children
     * @param dirties the dirty children (ids between 1 and n)
     * @return
     */
    public int run(int n, Set<Integer> dirties) {

        if (n < 1) {
            throw new IllegalArgumentException("n must be at least 1");
        }

        if (dirties.isEmpty()) {
            throw new IllegalArgumentException("dirties must contain at least one id");
        }

        for (int id : dirties) {
            if (id < 1 || id > n) {
                throw new IllegalArgumentException("dirtiest must only include ids between 1 and n");
            }
        }

        int sandboxId = 0;
        int parentId = n + 1;

        Simulator simulator = new Simulator(new StdOutLog());
        simulator.setLogMsgReceived(true);
        Network network = new Network(simulator);

        Link defaultLink = new Link(new FixedDuration(10));

        Sandbox sandbox = new Sandbox(sandboxId, simulator, network, dirties);
        SandboxParent parent = new SandboxParent(parentId, simulator, network);

        network.addNode(sandbox);
        network.addNode(parent);

        for (int i = 1; i <= n; i++) {
            SandboxChild child = new SandboxChild(i, simulator, network, sandboxId);
            network.addNode(child);
            network.addTwoWayLink(i, sandboxId, defaultLink);
            network.addTwoWayLink(i, parentId, defaultLink);
        }

        network.initTasks();

        while (parent.isWaitingForAnswer()) {
            simulator.step(50);
        }

        return parent.getAnswer();
    }

    public static void main(String[] args) {
        System.out.println("run1");
        int n = 5;
        Set<Integer> dirties = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        SandboxSystem instance = new SandboxSystem();
        int expResult = 5;
        int result = instance.run(n, dirties);
    }

}
