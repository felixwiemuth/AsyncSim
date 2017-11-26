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
 * Drops every k'th message.
 *
 * @author Felix Wiemuth
 */
public class PeriodicFailure implements Link.MsgFailure {

    private final int interval;
    private int cnt = 0;

    /**
     *
     * @param interval time between two failures
     */
    public PeriodicFailure(int interval) {
        this.interval = interval;
    }

    @Override
    public boolean isFailure(Message msg) {
        if (cnt == interval) {
            cnt = 0;
            return true;
        } else {
            cnt++;
            return false;
        }
    }
}
