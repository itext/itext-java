/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.processors.ITextProductEventProcessor;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;
import java.util.Map;

@Tag("UnitTest")
public class AbstractITextConfigurationEventTest extends ExtendedITextTest {

    @AfterEach
    public void after() {
        ProductEventHandler.INSTANCE.clearProcessors();
    }

    @Test
    public void addProcessorTest() {
        AbstractITextConfigurationEvent event = new TestAbstractITextConfigurationEvent();
        ITextProductEventProcessor processor = new TestITextProductEventProcessor();
        event.addProcessor(processor);

        Map<String, ITextProductEventProcessor> processors = ProductEventHandler.INSTANCE.getProcessors();
        Assertions.assertEquals(1, processors.size());
        Assertions.assertTrue(processors.values().contains(processor));
    }

    @Test
    public void getProcessorsTest() {
        AbstractITextConfigurationEvent event = new TestAbstractITextConfigurationEvent();
        ITextProductEventProcessor processor = new TestITextProductEventProcessor();
        event.addProcessor(processor);

        Assertions.assertEquals(ProductEventHandler.INSTANCE.getProcessors(), event.getProcessors());
    }

    @Test
    public void removeProcessorTest() {
        AbstractITextConfigurationEvent event = new TestAbstractITextConfigurationEvent();
        ITextProductEventProcessor processor = new TestITextProductEventProcessor();
        event.addProcessor(processor);

        event.removeProcessor(processor.getProductName());

        Map<String, ITextProductEventProcessor> processors = ProductEventHandler.INSTANCE.getProcessors();
        Assertions.assertEquals(0, processors.size());
    }

    @Test
    public void getActiveProcessorTest() {
        AbstractITextConfigurationEvent event = new TestAbstractITextConfigurationEvent();
        ITextProductEventProcessor processor = new TestITextProductEventProcessor();
        event.addProcessor(processor);

        Assertions.assertEquals(processor, event.getActiveProcessor(processor.getProductName()));
    }

    @Test
    public void addEventTest() {
        AbstractITextConfigurationEvent configurationEvent = new TestAbstractITextConfigurationEvent();
        AbstractProductProcessITextEvent processEvent = new TestAbstractProductProcessITextEvent();
        SequenceId id = new SequenceId();
        configurationEvent.addEvent(id, processEvent);

        List<AbstractProductProcessITextEvent> events = ProductEventHandler.INSTANCE.getEvents(id);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals(processEvent, events.get(0));
    }

    @Test
    public void getEventsTest() {
        AbstractITextConfigurationEvent configurationEvent = new TestAbstractITextConfigurationEvent();
        SequenceId id = new SequenceId();
        configurationEvent.addEvent(id, new TestAbstractProductProcessITextEvent());
        configurationEvent.addEvent(id, new TestAbstractProductProcessITextEvent());

        Assertions.assertEquals(ProductEventHandler.INSTANCE.getEvents(id), configurationEvent.getEvents(id));
    }

    static class TestAbstractITextConfigurationEvent extends AbstractITextConfigurationEvent {
        @Override
        protected void doAction() {
            // Empty method.
        }
    }

    static class TestAbstractProductProcessITextEvent extends AbstractProductProcessITextEvent {

        public TestAbstractProductProcessITextEvent() {
            super(new SequenceId(),
                    new ProductData("test public product name", "test product name", "test version", 0, 1),
                    null, EventConfirmationType.ON_DEMAND);
        }

        @Override
        public String getEventType() {
            return "test event type";
        }
    }

    static class TestITextProductEventProcessor implements ITextProductEventProcessor {
        @Override
        public void onEvent(AbstractProductProcessITextEvent event) {
            // Empty method.
        }

        @Override
        public String getProductName() {
            return "test product";
        }

        @Override
        public String getUsageType() {
            return "test usage type";
        }

        @Override
        public String getProducer() {
            return "test producer";
        }
    }
}
