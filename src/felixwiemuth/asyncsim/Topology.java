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
 * Generates different topologies in networks.
 *
 * @author Felix Wiemuth
 */
public class Topology {

    public static void linkFully(Network network, int duration) {
        for (int i = 1; i <= network.size(); i++) {
            for (int j = 1; j <= network.size(); j++) {
                network.addLink(i, j, new FixedDuration(duration));
            }
        }
    }

    public static void addRing(Network network, int duration) {
        for (int i = 1; i < network.size(); i++) {
            network.addLink(i, i + 1, new FixedDuration(duration));
            network.addLink(network.size(), 1, new FixedDuration(duration));
        }
    }

}
