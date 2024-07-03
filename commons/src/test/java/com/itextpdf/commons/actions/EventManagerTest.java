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

import com.itextpdf.commons.actions.processors.DefaultProductProcessorFactory;
import com.itextpdf.commons.actions.processors.IProductProcessorFactory;
import com.itextpdf.commons.actions.processors.UnderAgplProductProcessorFactory;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.ecosystem.TestConfigurationEvent;
import com.itextpdf.commons.exceptions.AggregatedException;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class EventManagerTest extends ExtendedITextTest {

    @AfterEach
    public void afterEach() {
        ProductProcessorFactoryKeeper.restoreDefaultProductProcessorFactory();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = TestConfigurationEvent.MESSAGE)
    })
    public void configurationEventTest() {
        AssertUtil.doesNotThrow(() -> EventManager.getInstance().onEvent(new TestConfigurationEvent()));
    }

    @Test
    public void throwSomeExceptionsTest() {
        EventManager eventManager = EventManager.getInstance();
        IEventHandler handler1 = new ThrowArithmeticExpHandler();
        IEventHandler handler2 = new ThrowIllegalArgumentExpHandler();

        eventManager.register(handler1);
        eventManager.register(handler2);

        SequenceId sequenceId = new SequenceId();
        try {
            eventManager.onEvent(new ITextTestEvent(sequenceId, null, "test-event", ProductNameConstant.ITEXT_CORE));
        } catch (AggregatedException e) {
            Assertions.assertEquals("Error during event processing:\n"
                    + "0) ThrowArithmeticExpHandler\n"
                    + "1) ThrowIllegalArgumentExpHandler\n", e.getMessage());

            List<Exception> aggregatedExceptions = e.getAggregatedExceptions();

            Assertions.assertEquals(2, aggregatedExceptions.size());
            Assertions.assertEquals("ThrowArithmeticExpHandler", aggregatedExceptions.get(0).getMessage());
            Assertions.assertEquals("ThrowIllegalArgumentExpHandler", aggregatedExceptions.get(1).getMessage());
        }

        eventManager.unregister(handler1);
        eventManager.unregister(handler2);
    }

    @Test
    public void throwOneUncheckedExceptionsTest() {
        EventManager eventManager = EventManager.getInstance();
        IEventHandler handler1 = new ThrowArithmeticExpHandler();
        eventManager.register(handler1);
        try {
            SequenceId sequenceId = new SequenceId();
            Exception exception = Assertions.assertThrows(ArithmeticException.class,
                    () -> eventManager.onEvent(
                            new ITextTestEvent(sequenceId, null, "test-event", ProductNameConstant.ITEXT_CORE)));
            Assertions.assertEquals("ThrowArithmeticExpHandler", exception.getMessage());
        } finally {
            eventManager.unregister(handler1);
        }
    }

    @Test
    public void configureHandlersTest() {
        EventManager eventManager = EventManager.getInstance();
        IEventHandler handler = new ThrowArithmeticExpHandler();

        Assertions.assertFalse(eventManager.isRegistered(handler));

        eventManager.register(handler);
        Assertions.assertTrue(eventManager.isRegistered(handler));

        Assertions.assertTrue(eventManager.unregister(handler));
        Assertions.assertFalse(eventManager.isRegistered(handler));

        Assertions.assertFalse(eventManager.unregister(handler));
    }

    @Test
    public void turningOffAgplTest() {
        IProductProcessorFactory defaultProductProcessorFactory = ProductProcessorFactoryKeeper.getProductProcessorFactory();
        Assertions.assertTrue(defaultProductProcessorFactory instanceof DefaultProductProcessorFactory);
        EventManager.acknowledgeAgplUsageDisableWarningMessage();
        IProductProcessorFactory underAgplProductProcessorFactory1 = ProductProcessorFactoryKeeper.getProductProcessorFactory();
        Assertions.assertTrue(underAgplProductProcessorFactory1 instanceof UnderAgplProductProcessorFactory);
    }

    private static class ThrowArithmeticExpHandler implements IEventHandler {
        @Override
        public void onEvent(IEvent event) {
            throw new ArithmeticException("ThrowArithmeticExpHandler");
        }
    }

    private static class ThrowIllegalArgumentExpHandler implements IEventHandler {
        @Override
        public void onEvent(IEvent event) {
            throw new IllegalArgumentException("ThrowIllegalArgumentExpHandler");
        }
    }
}
