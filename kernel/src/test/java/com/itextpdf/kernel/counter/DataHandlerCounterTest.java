/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.counter;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.counter.data.EventData;
import com.itextpdf.kernel.counter.data.EventDataCacheComparatorBased;
import com.itextpdf.kernel.counter.data.EventDataHandler;
import com.itextpdf.kernel.counter.data.EventDataHandlerUtil;
import com.itextpdf.kernel.counter.data.IEventDataFactory;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class DataHandlerCounterTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void disableHooksTest() throws InterruptedException {
        final int betweenChecksSleepTime = 500;
        final int handlerSleepTime = 100;
        TestDataHandler dataHandler = new TestDataHandler(handlerSleepTime);
        TestDataHandlerCounter counter = new TestDataHandlerCounter(dataHandler);

        // check the initial process count
        Assert.assertEquals(0, dataHandler.getProcessCount());

        long count = dataHandler.getProcessCount();
        Thread.sleep(betweenChecksSleepTime);
        // check that process count has been updated
        Assert.assertNotEquals(count, dataHandler.getProcessCount());

        counter.close();
        // ensure that last process on disable would be finished
        Thread.sleep(betweenChecksSleepTime);
        long totalCount = dataHandler.getProcessCount();
        Thread.sleep(betweenChecksSleepTime);
        // ensure that after disabling there are no new processes has been invoked
        Assert.assertEquals(totalCount, dataHandler.getProcessCount());
    }

    @Test
    public void onEventAfterDisableTest() throws InterruptedException {
        TestDataHandlerCounter counter = new TestDataHandlerCounter(new TestDataHandler(100));
        TestEvent testEvent = new TestEvent("test");

        AssertUtil.doesNotThrow(() -> counter.onEvent(testEvent, null));

        counter.close();

        junitExpectedException.expect(IllegalStateException.class);
        junitExpectedException.expectMessage(PdfException.DataHandlerCounterHasBeenDisabled);
        counter.onEvent(testEvent, null);
    }

    @Test
    public void multipleRegisterHooksTest() {
        TestDataHandler dataHandler = new TestDataHandler(200);
        TestDataHandlerCounter counter = new TestDataHandlerCounter(dataHandler);
        TestDataHandlerCounter secondCounter = new TestDataHandlerCounter(dataHandler);

        AssertUtil.doesNotThrow(() -> counter.close());
        AssertUtil.doesNotThrow(() -> secondCounter.close());
    }

    @Test
    // count set explicitly as it is required that the test should log this message only once
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNEXPECTED_EVENT_HANDLER_SERVICE_THREAD_EXCEPTION,
                    count = 1, logLevel = LogLevelConstants.ERROR)
    })
    public void timedProcessWithExceptionTest() throws InterruptedException {
        final int betweenChecksSleepTime = 500;
        final int handlerSleepTime = 100;
        TestDataHandlerWithException dataHandler = new TestDataHandlerWithException(handlerSleepTime);
        TestDataHandlerCounter counter = new TestDataHandlerCounter(dataHandler);

        // check the initial process count
        Assert.assertEquals(0, dataHandler.getProcessCount());
        Thread.sleep(betweenChecksSleepTime);
        // check that process count has not been updated
        Assert.assertEquals(0, dataHandler.getProcessCount());

        AssertUtil.doesNotThrow(() -> counter.close());
    }

    private static class TestDataHandlerCounter extends DataHandlerCounter<String, SimpleData> {

        public TestDataHandlerCounter(TestDataHandler dataHandler) {
            super(dataHandler);
        }
    }

    private static class SimpleData extends EventData<String> {

        public SimpleData(String signature) {
            super(signature);
        }
    }

    private static class SimpleDataFactory implements IEventDataFactory<String, SimpleData> {

        @Override
        public SimpleData create(IEvent event, IMetaInfo metaInfo) {
            return new SimpleData(event.getEventType());
        }
    }

    private static class TestDataHandler extends EventDataHandler<String, SimpleData> {

        private final AtomicLong processCount = new AtomicLong(0);

        public TestDataHandler(long sleepTime) {
            super(new EventDataCacheComparatorBased<String, SimpleData>(
                            new EventDataHandlerUtil.BiggerCountComparator<String, SimpleData>()),
                    new SimpleDataFactory(), sleepTime, sleepTime);
        }

        @Override
        public void tryProcessNext() {
            processCount.incrementAndGet();
            super.tryProcessNext();
        }

        @Override
        public void tryProcessRest() {
            processCount.incrementAndGet();
            super.tryProcessRest();
        }

        @Override
        protected boolean process(SimpleData data) {
            return true;
        }

        public long getProcessCount() {
            return processCount.get();
        }
    }

    private static class TestDataHandlerWithException extends TestDataHandler {

        public TestDataHandlerWithException(long sleepTime) {
            super(sleepTime);
        }

        @Override
        public void tryProcessNextAsync(Boolean daemon) {
            throw new PdfException("Some exception message");
        }
    }

    private static class TestEvent implements IEvent {

        private final String type;

        public TestEvent(String type) {
            this.type = type;
        }

        @Override
        public String getEventType() {
            return type;
        }
    }
}
