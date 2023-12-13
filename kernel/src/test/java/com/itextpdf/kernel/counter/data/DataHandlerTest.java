/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.counter.data;

import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.LoggerFactory;

@Category(UnitTest.class)
public class DataHandlerTest extends ExtendedITextTest {

    private static final int SUCCESS_LIMIT = 3;

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Process event with signature: type1, and count: 1"),
            @LogMessage(messageTemplate = "Process event with signature: type1, and count: 2", count = 2),
            @LogMessage(messageTemplate = "Process event with signature: type1, and count: 3"),
            @LogMessage(messageTemplate = "Process event with signature: type1, and count: 4"),
            @LogMessage(messageTemplate = "Process event with signature: type2, and count: 2", count = 2),
    })
    public void runTest() throws InterruptedException {
        TestDataHandler dataHandler = new TestDataHandler();
        dataHandler.register(new TestEvent("type1"), null);
        Thread.sleep(200);
        dataHandler.register(new TestEvent("type1"), null);
        Thread.sleep(200);
        dataHandler.register(new TestEvent("type2"), null);
        Thread.sleep(200);
        dataHandler.register(new TestEvent("type1"), null);
        Thread.sleep(200);
        dataHandler.register(new TestEvent("type1"), null);
        Thread.sleep(200);
        dataHandler.register(new TestEvent("type2"), null);
        Thread.sleep(200);
        dataHandler.tryProcessRest();
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

    private static class TestDataHandler extends EventDataHandler<String, SimpleData> {

        public TestDataHandler() {
            super(new EventDataCacheComparatorBased<String, SimpleData>(new EventDataHandlerUtil.BiggerCountComparator<String, SimpleData>()),
                    new SimpleDataFactory(), 0, 0);
        }

        @Override
        protected boolean process(SimpleData data) {
            LoggerFactory.getLogger(getClass()).warn("Process event with signature: " + data.getSignature() + ", and count: " + data.getCount());
            return data.getCount() > SUCCESS_LIMIT;
        }
    }
}
