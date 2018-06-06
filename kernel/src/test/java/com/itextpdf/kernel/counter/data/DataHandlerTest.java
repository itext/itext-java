package com.itextpdf.kernel.counter.data;

import com.itextpdf.kernel.counter.event.IEvent;
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
        dataHandler.register(new TestEvent("type1"));
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type1"));
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type2"));
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type1"));
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type1"));
        Thread.sleep(100);
        dataHandler.register(new TestEvent("type2"));
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
        public SimpleData create(IEvent event) {
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
