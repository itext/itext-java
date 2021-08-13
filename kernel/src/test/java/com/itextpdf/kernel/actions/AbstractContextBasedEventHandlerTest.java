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
package com.itextpdf.kernel.actions;

import com.itextpdf.events.IBaseEvent;
import com.itextpdf.events.ProductNameConstant;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.actions.ecosystem.TestMetaInfo;
import com.itextpdf.events.sequence.SequenceId;
import com.itextpdf.kernel.counter.context.IContext;
import com.itextpdf.kernel.counter.context.UnknownContext;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AbstractContextBasedEventHandlerTest extends ExtendedITextTest {

    @Test
    public void coreEventProcessedByHandlerTest() {
        TestEventHandler handler = new TestEventHandler(UnknownContext.PERMISSIVE);
        handler.onEvent(new ITextTestEvent(new SequenceId(), null,
                "test-event",
                ProductNameConstant.ITEXT_CORE));
        Assert.assertTrue(handler.wasInvoked());
    }

    @Test
    public void anotherProductEventNotProcessedByHandlerTest() {
        TestEventHandler handler = new TestEventHandler(UnknownContext.PERMISSIVE);
        handler.onEvent(new ITextTestEvent(new SequenceId(), null,
                "test-event",
                ProductNameConstant.PDF_HTML));
        Assert.assertFalse(handler.wasInvoked());
    }

    @Test
    public void eventWithMetaInfoTest() {
        TestEventHandler handler = new TestEventHandler(UnknownContext.PERMISSIVE);
        handler.onEvent(new ITextTestEvent(new SequenceId(), new TestMetaInfo("meta info from iTextCore"),
                "test-event",
                ProductNameConstant.ITEXT_CORE));
        Assert.assertTrue(handler.wasInvoked());
    }

    @Test
    public void notITextEventIsIgnoredTest() {
        TestEventHandler handler = new TestEventHandler(UnknownContext.PERMISSIVE);
        handler.onEvent(new UnknownEvent());
        Assert.assertFalse(handler.wasInvoked());
    }

    private static class TestEventHandler extends AbstractContextBasedEventHandler {
        private boolean invoked = false;
        public TestEventHandler(IContext onUnknownContext) {
            super(onUnknownContext);
        }

        @Override
        protected void onAcceptedEvent(AbstractContextBasedITextEvent event) {
            invoked = true;
        }

        public boolean wasInvoked() {
            return invoked;
        }
    }

    private static class UnknownEvent implements IBaseEvent {

    }
}
