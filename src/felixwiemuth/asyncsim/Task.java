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

package felixwiemuth.asyncsim;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author Felix Wiemuth
 */
public class Task {

    public interface Guard {

        boolean check();
    }

    public abstract class Action {

        private final Duration duration;

        public Action(Duration duration) {
            this.duration = duration;
        }

        public long getDuration() {
            return duration.getDuration();
        }

        /**
         * Simplification: is run atomically at one point in time. Note: Actions
         * can be nested.
         */
        public abstract void run();
    }

    public class Command {

        private final Guard guard;
        private final Action action;

        public Command(Guard guard, Action action) {
            this.guard = guard;
            this.action = action;
        }

        public boolean canRun() {
            return guard.check();
        }

        public void run() {
            simulator.addEvent(action.getDuration(), new Runnable() {
                @Override
                public void run() {
                    action.run();
                    busy = false;
                    msgQueue.addAll(busyMsgQueue);
                    busyMsgQueue.clear();
                    schedule();
                }
            });
            busy = true;
        }
    }

    private final int id;
    private final Simulator simulator;
    private final Network network;
    private final Queue<Message> msgQueue = new ArrayDeque<>(); // message queue as visible to the task
    private final Queue<Message> busyMsgQueue = new ArrayDeque<>(); // queue for messages received during the execution of an action
    private final List<Command> commands = new ArrayList<>();
    private boolean busy = false;

    public Task(int id, Simulator simulator, Network network) {
        this.id = id;
        this.simulator = simulator;
        this.network = network;
    }

    protected void addCmd(Command cmd) {
        commands.add(cmd);
    }

    /**
     * Will be executed when the distributed system starts running.
     */
    protected void onInit() {

    }

    /**
     * Checks which guards are satisfied and runs one of those randomly.
     */
    public void schedule() {
        if (busy) {
            return;
        }
        List<Command> canRun = new ArrayList<>();
        for (Command cmd : commands) {
            if (cmd.canRun()) {
                canRun.add(cmd);
            }
        }
        if (!canRun.isEmpty()) {
            // Choose one command of those in canRun randomly
            Command cmd = canRun.get(simulator.getRandom().nextInt(canRun.size()));
            cmd.run();
        }
    }

    public int getId() {
        return id;
    }

    public Set<Integer> getNeighbors() {
        return network.getNeighbors(getId());
    }

    public Set<Integer> getNeighbors(int src) {
        return network.getNeighbors(src);
    }

    public void addMsg(Message msg) {
        if (busy) {
            busyMsgQueue.add(msg);
        } else {
            msgQueue.add(msg);
            schedule();
        }
    }

    protected Message peekMsg() {
        return msgQueue.peek();
    }

    protected Message pollMsg() {
        return msgQueue.poll();
    }

    protected void sendMsg(int dest, Object data) {
        network.sendMsg(new Message(id, dest, data));
    }

    protected void log(String msg) {
        simulator.log(String.format("%2d: %s", getId(), msg));
    }
}
