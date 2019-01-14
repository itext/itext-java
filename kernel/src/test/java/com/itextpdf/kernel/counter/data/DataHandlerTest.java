/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type1"), null);
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type2"), null);
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type1"), null);
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type1"), null);
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type2"), null);
        Thread.sleep(100);
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
