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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.ecosystem.TestConfigurationEvent;
import com.itextpdf.commons.exceptions.AggregatedException;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class EventManagerTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
            Assert.assertEquals("Error during event processing:\n"
                    + "0) ThrowArithmeticExpHandler\n"
                    + "1) ThrowIllegalArgumentExpHandler\n", e.getMessage());

            List<Exception> aggregatedExceptions = e.getAggregatedExceptions();

            Assert.assertEquals(2, aggregatedExceptions.size());
            Assert.assertEquals("ThrowArithmeticExpHandler", aggregatedExceptions.get(0).getMessage());
            Assert.assertEquals("ThrowIllegalArgumentExpHandler", aggregatedExceptions.get(1).getMessage());
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

            junitExpectedException.expect(ArithmeticException.class);
            junitExpectedException.expectMessage("ThrowArithmeticExpHandler");
            eventManager.onEvent(new ITextTestEvent(sequenceId, null, "test-event", ProductNameConstant.ITEXT_CORE));
        } finally {
            eventManager.unregister(handler1);
        }
    }

    @Test
    public void configureHandlersTest() {
        EventManager eventManager = EventManager.getInstance();
        IEventHandler handler = new ThrowArithmeticExpHandler();

        Assert.assertFalse(eventManager.isRegistered(handler));

        eventManager.register(handler);
        Assert.assertTrue(eventManager.isRegistered(handler));

        Assert.assertTrue(eventManager.unregister(handler));
        Assert.assertFalse(eventManager.isRegistered(handler));

        Assert.assertFalse(eventManager.unregister(handler));

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
