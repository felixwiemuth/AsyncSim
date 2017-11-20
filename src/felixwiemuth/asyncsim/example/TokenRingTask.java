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

/**
 * A task that wants to perform an operation which requires exclusive resources
 * ("critical region"). The access to those resources is regulated with a token
 * which is passed along between tasks.
 *
 * @author Felix Wiemuth
 */
public class TokenRingTask extends Task {

    private final int LOAD_GENERATOR; // id of load generator
    private boolean crNeeded = false; // critical region needed

    public TokenRingTask(int id, int loadGenerator, Simulator simulator, Network network, long delay) {
        super(id, simulator, network);
        this.LOAD_GENERATOR = loadGenerator;
        addCmd(new Task.Command(
                new Guard() {
            @Override
            public boolean check() {
                Message m = peekMsg();
                return m != null && m.getSrc() == LOAD_GENERATOR && m.getData().equals("load");
            }
        },
                new Action(new FixedDuration(delay)) {
            @Override
            public void run() {
                pollMsg();
                log("Received load");
                crNeeded = true;
            }
        }
        ));

        addCmd(new Task.Command(
                new Guard() {
            @Override
            public boolean check() {
                Message m = peekMsg();
                return m != null && m.getSrc() != LOAD_GENERATOR && m.getData().equals("token");
            }
        },
                new Action(new FixedDuration(delay)) {
            @Override
            public void run() {
                pollMsg();
                log("Received token");
                if (crNeeded) {
                    log("Doing something in critical region");
                    crNeeded = false;
                }
                // Send token to first neighbor
                int tokenReceiver = getNeighbors().iterator().next();
                log("Sending token to " + tokenReceiver);
                sendMsg(tokenReceiver, "token");
            }
        }
        ));
    }

    @Override
    protected void onInit() {
        if (getId() == 1) {
            for (int dest : getNeighbors()) {
                sendMsg(dest, "token");
            }
        }
    }
}
