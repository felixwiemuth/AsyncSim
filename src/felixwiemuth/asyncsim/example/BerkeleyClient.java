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
import felixwiemuth.asyncsim.Task.Action;

/**
 *
 * @author Felix Wiemuth
 */
public class BerkeleyClient extends Task {

    private int localTime = 0;

    private final Action incrementLocalTime = new Task.Action() {
        @Override
        public void run() {
            localTime += 1000;
            scheduleAction(this, new RandomDuration(getRandom(), 10, 2));
        }
    };

    public BerkeleyClient(int id, Simulator simulator, Network network) {
        super(id, simulator, network);

        /*
         * Reply to time request from server.
         */
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return nextMsgEquals("getTime");
            }
        }, new Action() {
            @Override
            public void run() {
                Message msg = pollMsg();
                sendMsg(msg.getSrc(), localTime);
                log("Sent local time " + localTime);
            }
        }));

        /*
         * Update local time when receiving diff from server.
         */
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return nextMsgNotEquals("getTime");
            }
        }, new Action() {
            @Override
            public void run() {
                int diff = (int) pollMsg().getData();
                localTime += diff;
                log("Updated local time by " + diff + " to " + localTime);
            }
        }));
    }

    @Override
    protected void onInit() {
        scheduleAction(incrementLocalTime, new FixedDuration(0));
    }

}
