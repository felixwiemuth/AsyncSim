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

import java.util.Random;

/**
 * @author Felix Wiemuth
 */
public class RandomDuration implements Duration {

    private final Random random;
    private final long mean;
    private final double variation;

    public RandomDuration(Random random, long mean, double variation) {
        this.random = random;
        this.mean = mean;
        this.variation = variation;
    }

    @Override
    public long getDuration() {
        long duration = -1;
        while (duration < 0 || duration > 2 * mean) { // makes sure that duration is within [0, 2*mean] (and thus symmetrically distributed around mean)
            double nextGaussian = random.nextGaussian();
            duration = (long) ((nextGaussian * variation) + mean);
        }
        return 0;
    }
}
