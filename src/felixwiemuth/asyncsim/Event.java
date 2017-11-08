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
 * @author Felix Wiemuth
 */
public class Event implements Comparable<Event> {

    private final long time;
    private final Runnable runnable;

    public Event(long time, Runnable runnable) {
        this.time = time;
        this.runnable = runnable;
    }

    //NOTE: equals and hashCode can and should stay those of Object, then compareTo is consistent with equals
    @Override
    public int compareTo(Event o) {
        int compare = Long.compare(time, o.time);
        if (compare == 0) { // This makes compareTo consistent with equals
            return hashCode() < o.hashCode() ? -1 : 1;
        } else {
            return compare;
        }
    }

    public void run() {
        runnable.run();
    }

    public long getTime() {
        return time;
    }
}
