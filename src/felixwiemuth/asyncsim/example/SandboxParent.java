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

import felixwiemuth.asyncsim.Duration;
import felixwiemuth.asyncsim.FixedDuration;
import felixwiemuth.asyncsim.Message;
import felixwiemuth.asyncsim.Network;
import felixwiemuth.asyncsim.Simulator;
import felixwiemuth.asyncsim.Task;

/**
 * Models the parent.
 *
 * @author Felix Wiemuth
 */
public class SandboxParent extends Task {

    private static final Duration interval = new FixedDuration(100);
    private boolean waitingForAnswer = true;
    private int answer = -1;

    private final Action askChildren = new Action() {
        @Override
        public void run() {
            if (waitingForAnswer) {
                for (int child : getNeighbors()) {

                    sendMsg(child, "How many dirties?");
                }
                scheduleAction(this, interval);
            }
        }
    };

    public SandboxParent(int id, Simulator simulator, Network network) {
        super(id, simulator, network);

        /*
         * Receive the answer of a child.
         */
        addCmd(new Command(new Guard() {
            @Override
            public boolean check() {
                return hasMsg();
            }
        }, new Action() {
            @Override
            public void run() {
                Message msg = pollMsg();
                answer = (int) msg.getData();
                waitingForAnswer = false;
            }
        }));
    }

    @Override
    protected void onInit() {
        for (int child : getNeighbors()) {
            sendMsg(child, "Get ready");
        }
        // Wait until all children have probably initialized, then start to ask
        scheduleAction(askChildren, interval);
    }

    public boolean isWaitingForAnswer() {
        return waitingForAnswer;
    }

    public int getAnswer() {
        return answer;
    }
}
