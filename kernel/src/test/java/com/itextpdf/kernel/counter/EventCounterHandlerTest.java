package com.itextpdf.kernel.counter;

import com.itextpdf.kernel.counter.event.CoreEvent;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.LoggerFactory;

@Category(UnitTest.class)
public class EventCounterHandlerTest extends ExtendedITextTest {

    private static final int COUNT = 100;

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = "Process event: core-process", count = COUNT)})
    public void testCoreEvent() {
        IEventCounterFactory counterFactory = new SimpleEventCounterFactory(new ToLogCounter());
        EventCounterHandler.getInstance().register(counterFactory);
        for (int i = 0; i < COUNT; ++i) {
            EventCounterHandler.getInstance().onEvent(CoreEvent.PROCESS, getClass());
        }
        EventCounterHandler.getInstance().unregister(counterFactory);
    }

    @Test
    public void testUnknownEvent() {
        IEventCounterFactory counterFactory = new SimpleEventCounterFactory(new ToLogCounter());
        EventCounterHandler.getInstance().register(counterFactory);
        IEvent unknown = new UnknownEvent();
        for (int i = 0; i < COUNT; ++i) {
            EventCounterHandler.getInstance().onEvent(unknown, getClass());
        }
        EventCounterHandler.getInstance().unregister(counterFactory);
    }

    private static class ToLogCounter extends EventCounter {

        @Override
        protected void process(IEvent event) {
            LoggerFactory.getLogger(getClass()).warn("Process event: " + event.getEventType());
        }
    }

    private static class UnknownEvent implements IEvent {

        @Override
        public String getEventType() {
            return "unknown";
        }
    }
}
