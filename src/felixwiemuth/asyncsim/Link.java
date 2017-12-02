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

/**
 * Represents a link in the network. Can simulate failure of delivering
 * messages, either per-message ({@link MsgFailure}) or by being set into
 * {@link State#DEAD} mode. If a log is set, failures are logged.
 *
 * @author Felix Wiemuth
 */
public class Link {

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

    public interface MsgFailure {

        /**
         * Determines whether a message should be dropped. Can for example
         * decide with a fixed probability for each message or drop every kth
         * message.
         *
         * @param msg
         * @return
         */
        boolean isFailure(Message msg);
    }

    private final Duration duration;
    private State state = State.ALIVE;
    private MsgFailure failure;

    public Link(Duration duration) {
        this.duration = duration;
    }

    public Link(long duration) {
        this.duration = new FixedDuration(duration);
    }

    public Link(Duration duration, MsgFailure failure) {
        this.duration = duration;
        this.failure = failure;
    }

    public Link(long duration, MsgFailure failure) {
        this(duration);
        this.failure = failure;
    }

    /**
     * Set this link's {@link MsgFailure}.
     *
     * @param failure the {@link MsgFailure} to be used, can be null.
     */
    public void setMsgFailure(MsgFailure failure) {
        this.failure = failure;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * Send a message over this link. Puts the message into the receiver's
     * message queue or drops it if the link is in dead {@link State} or a
     * message failure occurs.
     *
     * @param msg the message to be transferred
     * @param dest
     * @return A log entry if the message was dropped or null otherwise.
     */
    public String sendMsg(Simulator simulator, Message msg, Task dest) {
        String logEntry = null;
        if (state == State.DEAD) {
            logEntry = "link dead";
        } else if (failure != null && failure.isFailure(msg)) {
            logEntry = "delivery failure";
        } else {
            simulator.addEvent(duration.getDuration(), new Runnable() {
                @Override
                public void run() {
                    dest.addMsg(msg);
                    simulator.logMsgReceived(msg);
                }
            });
            return null;
        }
        if (logEntry != null) {
            logEntry = "Message from " + msg.getSrc() + " to " + msg.getDest() + " dropped (" + logEntry + ").";
        }
        return logEntry;
    }
}
