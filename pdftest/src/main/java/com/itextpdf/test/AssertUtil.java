/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import java.util.function.Supplier;
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
     * Assert that the assertion passed within the timeout
     *
     * @param assertion callback to the actual asserts to be safeguarded
     * @param timeout the maximum time it can take before passing the assertions
     *
     * @throws Exception any exception thrown by the assertion callback
     */
    public static void assertPassedWithinTimeout(ThrowingRunnable assertion, Duration timeout) throws Exception {
        assertPassedWithinTimeout(assertion, timeout, Duration.ZERO);
    }

    /**
     * Assert that the assertion passed within the timeout
     *
     * @param assertion callback to the actual asserts to be safeguarded
     * @param timeout the maximum time it can take before passing the assertions
     * @param sleepTime the time to sleep between polls
     *
     * @throws Exception any exception thrown by the assertion callback
     */
    public static void assertPassedWithinTimeout(ThrowingRunnable assertion,
            Duration timeout, Duration sleepTime) throws Exception {
        assertPassedWithinTimeout(assertion, timeout, () -> sleepTime.toMillis());
    }

    /**
     * Assert that the assertion passed within the random timeout.
     *
     * <p>
     * This method may be useful when your tests fetch some stateful resources concurrently. It allows to de-synchronize
     * access from different clients. It's exponential backoff with jitter but without 'exponential' part. Because
     * it does not really care if tests become longer, the main goal is to pass the tests.
     *
     * @param assertion callback to the actual asserts to be safeguarded
     * @param timeout the maximum time it can take before passing the assertions
     * @param maxSleepTime the maximum time to sleep between polls
     *
     * @throws Exception any exception thrown by the assertion callback
     */
    public static void assertPassedWithinRandomTimeout(ThrowingRunnable assertion,
            Duration timeout, Duration maxSleepTime) throws Exception {
        assertPassedWithinTimeout(assertion, timeout, () -> Math.round(maxSleepTime.toMillis() * Math.random()));
    }

    private static void assertPassedWithinTimeout(ThrowingRunnable assertion,
            Duration timeout, Supplier<Long> timeoutCalc) throws Exception {
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
                Thread.sleep(timeoutCalc.get());
                // Ignore assertion failure if timeout has not passed
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
