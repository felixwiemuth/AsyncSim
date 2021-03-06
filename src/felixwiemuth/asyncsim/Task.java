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
import java.util.Random;
import java.util.Set;

/**
 * A program consisting of {@link Guard}s and {@link Action}s. When in
 * {@link State#ALIVE} state will execute the corresponding action whenever a
 * guard is true. When state is set to {@link State#DEAD} while an action is
 * running, the action will be aborted without any effects of its so far
 * execution being visible.
 *
 * @author Felix Wiemuth
 */
public class Task {

    public interface Guard {

        /**
         * Returns true if the corresponding {@link Action} should be executed.
         * Does not modify the state of a {@link Task}.
         *
         * @return
         */
        boolean check();
    }

    public abstract class Action {

        private final Duration duration;

        public Action() {
            duration = null;
        }

        public Action(Duration duration) {
            this.duration = duration;
        }

        public boolean hasDuration() {
            return duration != null;
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

        public Action getAction() {
            return action;
        }
    }

    public static enum State {
        /**
         * The link delivers all messages.
         */
        ALIVE,
        /**
         * The link does not deliver any messages.
         */
        DEAD
    }

    private final int id;
    private final Simulator simulator;
    private final Network network;
    private State state = State.ALIVE;
    private final Queue<Message> msgQueue = new ArrayDeque<>(); // message queue as visible to the task
    private final Queue<Message> busyMsgQueue = new ArrayDeque<>(); // queue for messages received during the execution of an action
    private final List<Command> commands = new ArrayList<>();
    private final List<Action> waitingActions = new ArrayList<>(); // actions waiting for execution (added by task itself)
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

    public void setState(State state) {
        this.state = state;
        if (state == State.ALIVE) {
            schedule();
        } else {
            busy = false;
        }
    }

    /**
     * Checks which guards are satisfied and randomly chooses one of those or a
     * waiting action to run.
     */
    public void schedule() {
        if (busy || state == State.DEAD) {
            return;
        }
        List<Action> canRun = new ArrayList<>();
        for (Command cmd : commands) {
            if (cmd.canRun()) {
                canRun.add(cmd.getAction());
            }
        }
        canRun.addAll(waitingActions);
        if (!canRun.isEmpty()) {
            // Choose one command of those in canRun randomly
            Action action = canRun.get(simulator.getRandom().nextInt(canRun.size()));
            waitingActions.remove(action); // remove action from list of waiting actions if it was chosen from there
            Runnable code = new Runnable() {
                @Override
                public void run() {
                    if (state == State.ALIVE) {
                        action.run();
                        busy = false;
                        msgQueue.addAll(busyMsgQueue);
                        busyMsgQueue.clear();
                        schedule();
                    } // if task died while executing the action, it won't be performed
                }
            };
            if (action.hasDuration()) {
                simulator.addEvent(action.getDuration(), code);
            } else {
                simulator.addEvent(code);
            }
            busy = true;
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
        if (state == State.ALIVE) {
            if (busy) {
                busyMsgQueue.add(msg);
            } else {
                msgQueue.add(msg);
                schedule();
            }
        } // else ignore message
    }

    protected Message peekMsg() {
        return msgQueue.peek();
    }

    protected Message pollMsg() {
        return msgQueue.poll();
    }

    protected boolean hasMsg() {
        return peekMsg() != null;
    }

    protected boolean nextMsgEquals(Object o) {
        return peekMsg() != null && peekMsg().getData().equals(o);
    }

    protected boolean nextMsgNotEquals(Object o) {
        return peekMsg() != null && !peekMsg().getData().equals(o);
    }

    protected boolean nextMsgSrcIs(int src) {
        return peekMsg() != null && peekMsg().getSrc() == src;
    }

    protected void sendMsg(int dest, Object data) {
        network.sendMsg(new Message(id, dest, data));
    }

    /**
     * Schedule an {@link Action} to be executed by this task after a given
     * delay.
     *
     * @param action
     * @param delay
     */
    protected void scheduleAction(Action action, Duration delay) {
        simulator.addEvent(delay.getDuration(), new Runnable() {
            @Override
            public void run() {
                waitingActions.add(action);
                schedule();
            }
        });
    }

    protected Random getRandom() {
        return simulator.getRandom();
    }

    protected void log(String msg) {
        simulator.log(String.format("%2d: %s", getId(), msg));
    }
}
