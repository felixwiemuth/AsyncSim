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

/**
 * Models the child.
 *
 * @author Felix Wiemuth
 */
public class SandboxChild extends Task {

    private int count = 0; // counting how many times the parent has asked without us answering
    private int dirtiesSeen;

    public SandboxChild(int id, Simulator simulator, Network network, int sandbox) {
        super(id, simulator, network);

        /*
         * When the parent says "get ready" look how many dirty children we see.
         */
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return peekMsg() != null && peekMsg().getData() == "Get ready";
            }
        }, new Action() {
            @Override
            public void run() {
                pollMsg();
                sendMsg(sandbox, "How many dirties can I see?");
            }
        }));

        /*
         * Handle answer of sandbox.
         */
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return peekMsg() != null && peekMsg().getSrc() == sandbox;
            }
        }, new Action() {
            @Override
            public void run() {
                dirtiesSeen = (int) pollMsg().getData();
            }
        }));

        /*
         * Receive the parent's question and answer if knowing how many children
         * are dirty.
         */
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return peekMsg() != null && peekMsg().getData().equals("How many dirties?");
            }
        }, new Action() {
            @Override
            public void run() {
                Message msg = pollMsg();
                if (count == dirtiesSeen) {
                    sendMsg(msg.getSrc(), count + 1);
                }
                count++;
            }
        }));
    }

}
