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
import felixwiemuth.asyncsim.Simulator;
import felixwiemuth.asyncsim.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Felix Wiemuth
 */
public class FloodingTask extends Task {

    public FloodingTask(int id, Simulator simulator, Network network, long delay) {
        super(id, simulator, network);

        addCmd(new Command(
                new Guard() {
            @Override
            public boolean check() {
                return peekMsg() != null;
            }
        },
                new Action(new FixedDuration(delay)) {
            @Override
            public void run() {
                Message msg = pollMsg();
                String logmsg = "  " + id + ": Received " + ((String) msg.getData()) + ", sent to: ";
                List<Integer> neighbors = new ArrayList(network.getNeighbors(id));
                Collections.shuffle(neighbors, simulator.getRandom());
                String s = (String) msg.getData();
                s += id;
                for (int i = 0; i < 2; i++) {
                    sendMsg(neighbors.get(i), s);
                    logmsg += neighbors.get(i) + " ";
                }
                log(logmsg);
            }
        }));
    }
}
