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

import java.util.PriorityQueue;
import java.util.Random;

/**
 * @author Felix Wiemuth
 */
public class Simulator {

    private final Log log;
    private final Random random = new Random();
    private long time = 0;
    private final PriorityQueue<Event> events = new PriorityQueue<>();

    public Simulator(Log log) {
        this.log = log;
    }

    public void addEvent(long delay, Runnable runnable) {
        events.add(new Event(time + delay, runnable));
    }

    public boolean isFinished() {
        return events.isEmpty();
    }

    public void step() {
        //TODO check whether there are multiple events at the same time and execute in random order?
        if (!events.isEmpty()) {
            Event event = events.poll();
            time = event.getTime();
            event.run();
        }
    }

    public void step(int n) {
        for (int step = 0; step < n; step++) {
            step();
        }
    }

    public Random getRandom() {
        return random;
    }

    public void log(String msg) {
        log.log(time, msg);
    }
}
