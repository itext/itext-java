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
package com.itextpdf.commons.logs;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class LazyLoggerTest extends ExtendedITextTest {

    private static final Throwable TEST_EXCEPTION = new ITextException();

    @Test
    public void errorEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isErrorEnabled());
        logger.error(() -> testMessageProvider.provide());

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<String> calls = logStats.getErrorCalls();
        Assertions.assertEquals(1, calls.size());
        Assertions.assertEquals(TestStringProvider.MESSAGE, calls.get(0));
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void errorDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isErrorEnabled());
        logger.error(() -> testMessageProvider.provide());

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void errorWithExceptionEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isErrorEnabled());
        logger.error(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<Tuple2<String, Throwable>> calls = logStats.getErrorWithThrowableCalls();
        Assertions.assertEquals(1, calls.size());
        Tuple2<String, Throwable> call = calls.get(0);
        Assertions.assertEquals(TestStringProvider.MESSAGE, call.getFirst());
        Assertions.assertSame(TEST_EXCEPTION, call.getSecond());
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void errorWithExceptionDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isErrorEnabled());
        logger.error(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void warnEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isWarnEnabled());
        logger.warn(() -> testMessageProvider.provide());

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<String> calls = logStats.getWarnCalls();
        Assertions.assertEquals(1, calls.size());
        Assertions.assertEquals(TestStringProvider.MESSAGE, calls.get(0));
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void warnDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isWarnEnabled());
        logger.warn(() -> testMessageProvider.provide());

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void warnWithExceptionEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isWarnEnabled());
        logger.warn(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<Tuple2<String, Throwable>> calls = logStats.getWarnWithThrowableCalls();
        Assertions.assertEquals(1, calls.size());
        Tuple2<String, Throwable> call = calls.get(0);
        Assertions.assertEquals(TestStringProvider.MESSAGE, call.getFirst());
        Assertions.assertSame(TEST_EXCEPTION, call.getSecond());
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void warnWithExceptionDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isWarnEnabled());
        logger.warn(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void infoEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isInfoEnabled());
        logger.info(() -> testMessageProvider.provide());

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<String> calls = logStats.getInfoCalls();
        Assertions.assertEquals(1, calls.size());
        Assertions.assertEquals(TestStringProvider.MESSAGE, calls.get(0));
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void infoDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isInfoEnabled());
        logger.info(() -> testMessageProvider.provide());

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void infoWithExceptionEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isInfoEnabled());
        logger.info(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<Tuple2<String, Throwable>> calls = logStats.getInfoWithThrowableCalls();
        Assertions.assertEquals(1, calls.size());
        Tuple2<String, Throwable> call = calls.get(0);
        Assertions.assertEquals(TestStringProvider.MESSAGE, call.getFirst());
        Assertions.assertSame(TEST_EXCEPTION, call.getSecond());
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void infoWithExceptionDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isInfoEnabled());
        logger.info(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void debugEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isDebugEnabled());
        logger.debug(() -> testMessageProvider.provide());

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<String> calls = logStats.getDebugCalls();
        Assertions.assertEquals(1, calls.size());
        Assertions.assertEquals(TestStringProvider.MESSAGE, calls.get(0));
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void debugDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isDebugEnabled());
        logger.debug(() -> testMessageProvider.provide());

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void debugWithExceptionEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isDebugEnabled());
        logger.debug(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<Tuple2<String, Throwable>> calls = logStats.getDebugWithThrowableCalls();
        Assertions.assertEquals(1, calls.size());
        Tuple2<String, Throwable> call = calls.get(0);
        Assertions.assertEquals(TestStringProvider.MESSAGE, call.getFirst());
        Assertions.assertSame(TEST_EXCEPTION, call.getSecond());
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void debugWithExceptionDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isDebugEnabled());
        logger.debug(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void traceEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isTraceEnabled());
        logger.trace(() -> testMessageProvider.provide());

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<String> calls = logStats.getTraceCalls();
        Assertions.assertEquals(1, calls.size());
        Assertions.assertEquals(TestStringProvider.MESSAGE, calls.get(0));
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void traceDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isTraceEnabled());
        logger.trace(() -> testMessageProvider.provide());

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }

    @Test
    public void traceWithExceptionEnabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(true);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertTrue(logger.isTraceEnabled());
        logger.trace(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(1, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        List<Tuple2<String, Throwable>> calls = logStats.getTraceWithThrowableCalls();
        Assertions.assertEquals(1, calls.size());
        Tuple2<String, Throwable> call = calls.get(0);
        Assertions.assertEquals(TestStringProvider.MESSAGE, call.getFirst());
        Assertions.assertSame(TEST_EXCEPTION, call.getSecond());
        Assertions.assertEquals(1, logStats.getTotalInvocationsCount());
    }

    @Test
    public void traceWithExceptionDisabledTest() {
        TestStringProvider testMessageProvider = new TestStringProvider();
        TestLogger testLogger = new TestLogger(false);
        LazyLogger logger = new LazyLogger(testLogger);

        Assertions.assertFalse(logger.isTraceEnabled());
        logger.trace(() -> testMessageProvider.provide(), TEST_EXCEPTION);

        Assertions.assertEquals(0, testMessageProvider.getCallCount());

        TestLoggerStats logStats = testLogger.getStats();
        Assertions.assertEquals(0, logStats.getTotalInvocationsCount());
    }
}
