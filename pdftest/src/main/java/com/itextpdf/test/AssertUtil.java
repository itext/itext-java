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
package com.itextpdf.test;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

/**
 * Utilities class for assertion operation.
 */
public class AssertUtil {
    private AssertUtil() {
        // Empty constructor
    }

    /**
     * Asserts that {@link Executor#execute()} method call doesn't produce any
     * {@link Exception} otherwise test will fail by throwing {@link AssertionError}.
     *
     * @param executor the instance of {@link Executor} whose
     * {@link Executor#execute()} method will be checked for exception throwing
     */
    public static void doesNotThrow(Executor executor) {
        try {
            executor.execute();
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }

    /**
     * Asserts that {@link Executor#execute()} method call doesn't produce any
     * {@link Exception} otherwise test will fail by throwing {@link AssertionError}.
     *
     * @param executor the instance of {@link Executor} whose
     * {@link Executor#execute()} method will be checked for exception throwing
     * @param message the identifying message for the {@link AssertionError} may be null
     */
    public static void doesNotThrow(Executor executor, String message) {
        try {
            executor.execute();
        } catch (Exception ex) {
            Assertions.fail(message);
        }
    }


    /**
     * Assert that the assertion passed within the timeout.
     *
     * @param assertion Callback to the actuals asserts to be safeguarded.
     * @param timeout The maximum tilme it can take before passing the assertions.
     * @throws Exception Any exception thrown by the assertion callback.
     */
    public static void assertPassedWithinTimeout(ThrowingRunnable assertion, Duration timeout) throws Exception {
        assertPassedWithinTimeout(assertion, timeout, Duration.ZERO);
    }

    /**
     * Assert that the assertion passed within the timeout.
     *
     * @param assertion Callback to the actuals asserts to be safeguarded.
     * @param timeout The maximum tilme it can take before passing the assertions.
     * @param sleepTime The time to sleep between polls.
     * @throws Exception Any exception thrown by the assertion callback.
     */
    public static void assertPassedWithinTimeout(ThrowingRunnable assertion, Duration timeout, Duration sleepTime) throws Exception {
        long sleepTimeInMillis = sleepTime.toMillis();
        Instant start = Instant.now();
        boolean passed = false;
        while (!passed) {
            try {
                assertion.run();
                passed = true;
            }
            catch (AssertionFailedError| Exception e) {
                if (timeout.compareTo(Duration.between(start, Instant.now())) < 0) {
                    throw e;
                }
                // If sleepTimeInMillis is 0 then the thread will attempt to yield
                Thread.sleep(sleepTimeInMillis);
                //ignore assertion failure if timeout not spent.
            }
        }
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        /**
         * Runs this operation.
         *
         * @throws Exception any exception thrown by the runnable
         */
        void run() throws Exception;
    }
}
