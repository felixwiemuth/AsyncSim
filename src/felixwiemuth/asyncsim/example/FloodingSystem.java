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
public class FloodingSystem {

    public static void main(String[] args) {
        Simulator simulator = new Simulator(new StdOutLog());
        Network network = new Network(simulator);
        List<Task> tasks = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            Task t = new FloodingTask(i, simulator, network, i);
            network.addNode(t);
            tasks.add(t);
        }
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                network.addLink(i, j, new FixedDuration(i * j));
            }
        }

        tasks.get(0).addMsg(new Message(0, 1, "0"));
        tasks.get(0).schedule();

        simulator.step(1000);
    }
}
