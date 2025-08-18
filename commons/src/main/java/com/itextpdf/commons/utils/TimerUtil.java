/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.commons.utils;

import java.util.Timer;

/**
 * Utility class for creating and managing timers.
 */
public class TimerUtil {

    private TimerUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a new Timer instance.
     *
     * @param task   the task to be executed by the timer
     * @param delay  the delay before the task is executed for the first time
     * @param period the period between subsequent executions of the task
     *
     * @return a Timer instance that executes the task at the specified interval
     */

    public static Timer newTimerWithRecurringTask(Action task, long delay, long period) {
        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                task.execute();
            }
        }, delay, period);
        return t;
    }

    /**
     * Stops the given Timer instance.
     *
     * @param timer the Timer instance to stop
     */
    public static void stopTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

}
