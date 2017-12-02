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

import felixwiemuth.asyncsim.Message;
import felixwiemuth.asyncsim.Network;
import felixwiemuth.asyncsim.Simulator;
import felixwiemuth.asyncsim.Task;
import java.util.Set;

/**
 * Models what the children see. Allows each child to ask how many other dirty
 * children there are.
 *
 * @author Felix Wiemuth
 */
public class Sandbox extends Task {

    /**
     *
     * @param id
     * @param simulator
     * @param network
     * @param dirties the IDs of the children which are dirty
     */
    public Sandbox(int id, Simulator simulator, Network network, Set<Integer> dirties) {
        super(id, simulator, network);

        /*
         * When receiving a message, reply with the number of dirty children the
         * sender can see.
         */
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return peekMsg() != null;
            }
        }, new Action() {
            @Override
            public void run() {
                Message msg = pollMsg();
                int child = msg.getSrc();
                int dirtiesSeen = dirties.size();
                if (dirties.contains(child)) {
                    dirtiesSeen -= 1;
                }
                sendMsg(child, dirtiesSeen);
            }
        }));
    }

}
