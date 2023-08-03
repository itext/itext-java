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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.processors.AbstractITextProductEventProcessor;
import com.itextpdf.commons.actions.processors.IProductProcessorFactory;
import com.itextpdf.commons.actions.processors.ITextProductEventProcessor;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.exceptions.ProductEventHandlerRepeatException;
import com.itextpdf.commons.exceptions.UnknownProductException;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ProductEventHandlerTest extends ExtendedITextTest {
    @Before
    public void clearProcessors() {
        ProductEventHandler.INSTANCE.clearProcessors();
    }

    @After
    public void afterEach() {
        ProductProcessorFactoryKeeper.restoreDefaultProductProcessorFactory();
    }

    @Test
    public void unknownProductTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        AbstractContextBasedITextEvent event = new ITextTestEvent(new SequenceId(), null, "test-event", "Unknown Product");
        Exception ex = Assert.assertThrows(UnknownProductException.class,
                () -> handler.onAcceptedEvent(event));
        Assert.assertEquals(MessageFormatUtil.format(UnknownProductException.UNKNOWN_PRODUCT, "Unknown Product"),
                ex.getMessage());
    }

    @Test
    public void sequenceIdBasedEventTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        SequenceId sequenceId = new SequenceId();

        Assert.assertTrue(handler.getEvents(sequenceId).isEmpty());

        handler.onAcceptedEvent(new ITextTestEvent(sequenceId, null, "test-event",
                ProductNameConstant.ITEXT_CORE));

        Assert.assertEquals(1, handler.getEvents(sequenceId).size());

        AbstractProductProcessITextEvent event = handler.getEvents(sequenceId).get(0);
        Assert.assertEquals(sequenceId.getId(), event.getSequenceId().getId());
        Assert.assertNull(event.getMetaInfo());
        Assert.assertEquals("test-event", event.getEventType());
        Assert.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
    }

    @Test
    public void reportEventSeveralTimesTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        SequenceId sequenceId = new SequenceId();

        Assert.assertTrue(handler.getEvents(sequenceId).isEmpty());

        ITextTestEvent event = new ITextTestEvent(sequenceId, null, "test-event",
                ProductNameConstant.ITEXT_CORE);
        EventManager.getInstance().onEvent(event);

        Assert.assertEquals(1, handler.getEvents(sequenceId).size());
        Assert.assertEquals(event, handler.getEvents(sequenceId).get(0));

        EventManager.getInstance().onEvent(event);
        Assert.assertEquals(2, handler.getEvents(sequenceId).size());
        Assert.assertEquals(event, handler.getEvents(sequenceId).get(0));
        Assert.assertEquals(event, handler.getEvents(sequenceId).get(1));
    }

    @Test
    public void confirmEventTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        SequenceId sequenceId = new SequenceId();

        Assert.assertTrue(handler.getEvents(sequenceId).isEmpty());

        ITextTestEvent event = new ITextTestEvent(sequenceId, null, "test-event",
                ProductNameConstant.ITEXT_CORE);
        EventManager.getInstance().onEvent(event);

        ConfirmEvent confirmEvent = new ConfirmEvent(sequenceId, event);
        EventManager.getInstance().onEvent(confirmEvent);

        Assert.assertEquals(1, handler.getEvents(sequenceId).size());
        Assert.assertTrue(handler.getEvents(sequenceId).get(0) instanceof ConfirmedEventWrapper);
        Assert.assertEquals(event, ((ConfirmedEventWrapper) handler.getEvents(sequenceId).get(0)).getEvent());
    }

    @Test
    public void settingCustomProcessFactoryTest() {
        CustomFactory productProcessorFactory = new CustomFactory();
        productProcessorFactory.createProcessor(ProductNameConstant.ITEXT_CORE);
        ProductProcessorFactoryKeeper.setProductProcessorFactory(productProcessorFactory);

        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        ITextProductEventProcessor activeProcessor = handler.getActiveProcessor(ProductNameConstant.ITEXT_CORE);
        Assert.assertTrue(activeProcessor instanceof TestProductEventProcessor);
    }

    @Test
    public void repeatEventHandlingWithFiveExceptionOnProcessingTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        handler.addProcessor(new RepeatEventProcessor(5));

        AbstractContextBasedITextEvent event = new ITextTestEvent(new SequenceId(), null, "test",
                ProductNameConstant.ITEXT_CORE);

        Exception e = Assert.assertThrows(ProductEventHandlerRepeatException.class,
                () -> handler.onAcceptedEvent(event));
        Assert.assertEquals("customMessage5", e.getMessage());
    }

    @Test
    public void repeatEventHandlingWithFourExceptionOnProcessingTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        handler.addProcessor(new RepeatEventProcessor(4));

        AbstractContextBasedITextEvent event = new ITextTestEvent(new SequenceId(), null, "test",
                ProductNameConstant.ITEXT_CORE);

        AssertUtil.doesNotThrow(() -> handler.onAcceptedEvent(event));
    }

    @Test
    public void repeatEventHandlingWithOneExceptionOnProcessingTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        handler.addProcessor(new RepeatEventProcessor(1));

        AbstractContextBasedITextEvent event = new ITextTestEvent(new SequenceId(), null, "test",
                ProductNameConstant.ITEXT_CORE);

        AssertUtil.doesNotThrow(() -> handler.onAcceptedEvent(event));
    }

    private static class CustomFactory implements IProductProcessorFactory {
        @Override
        public ITextProductEventProcessor createProcessor(String productName) {
            return new TestProductEventProcessor(productName);
        }
    }

    private static class TestProductEventProcessor extends AbstractITextProductEventProcessor {
        public TestProductEventProcessor(String productName) {
            super(productName);
        }

        @Override
        public void onEvent(AbstractProductProcessITextEvent event) {
            // do nothing
        }

        @Override
        public String getUsageType() {
            return "AGPL";
        }
    }


    private static class RepeatEventProcessor implements ITextProductEventProcessor {
        private final int exceptionsCount;
        private int exceptionCounter = 0;

        public RepeatEventProcessor(int exceptionsCount) {
            this.exceptionsCount = exceptionsCount;
        }

        @Override
        public void onEvent(AbstractProductProcessITextEvent event) {
            if (exceptionCounter < exceptionsCount) {
                exceptionCounter++;
                throw new ProductEventHandlerRepeatException("customMessage" + exceptionCounter);
            }

        }

        @Override
        public String getProductName() {
            return ProductNameConstant.ITEXT_CORE;
        }

        @Override
        public String getUsageType() {
            return "someUsage";
        }

        @Override
        public String getProducer() {
            return "someProducer";
        }
    }
}
