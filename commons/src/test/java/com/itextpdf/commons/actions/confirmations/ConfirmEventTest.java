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
package com.itextpdf.commons.actions.confirmations;

import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.ecosystem.TestMetaInfo;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ConfirmEventTest extends ExtendedITextTest {
    @Test
    public void constructorWithSequenceIdTest() {
        SequenceId sequenceId = new SequenceId();
        ITextTestEvent iTextTestEvent = new ITextTestEvent(new SequenceId(), new TestMetaInfo(""), "eventType", "productName");
        ConfirmEvent confirmEvent = new ConfirmEvent(sequenceId, iTextTestEvent);

        Assertions.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());
        Assertions.assertEquals("eventType", confirmEvent.getEventType());
        Assertions.assertEquals("productName", confirmEvent.getProductName());
        Assertions.assertSame(sequenceId, confirmEvent.getSequenceId());
        Assertions.assertEquals(EventConfirmationType.UNCONFIRMABLE, confirmEvent.getConfirmationType());
        Assertions.assertNotNull(confirmEvent.getProductData());
        Assertions.assertEquals(ITextTestEvent.class, confirmEvent.getClassFromContext());
    }

    @Test
    public void constructorWithoutSequenceIdTest() {
        ITextTestEvent iTextTestEvent = new ITextTestEvent(new SequenceId(), new TestMetaInfo(""), "eventType", "productName");
        ConfirmEvent confirmEvent = new ConfirmEvent(iTextTestEvent);

        Assertions.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());
        Assertions.assertEquals("eventType", confirmEvent.getEventType());
        Assertions.assertEquals("productName", confirmEvent.getProductName());
        Assertions.assertSame(iTextTestEvent.getSequenceId(), confirmEvent.getSequenceId());
        Assertions.assertEquals(EventConfirmationType.UNCONFIRMABLE, confirmEvent.getConfirmationType());
        Assertions.assertNotNull(confirmEvent.getProductData());
        Assertions.assertEquals(ITextTestEvent.class, confirmEvent.getClassFromContext());
    }

    @Test
    public void confirmEventInsideOtherConfirmEventTest() {
        ITextTestEvent iTextTestEvent = new ITextTestEvent(new SequenceId(), new TestMetaInfo(""), "eventType", "productName");
        ConfirmEvent child = new ConfirmEvent(iTextTestEvent.getSequenceId(), iTextTestEvent);
        ConfirmEvent confirmEvent = new ConfirmEvent(child);

        Assertions.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());

        Assertions.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());
        Assertions.assertEquals("eventType", confirmEvent.getEventType());
        Assertions.assertEquals("productName", confirmEvent.getProductName());
        Assertions.assertSame(iTextTestEvent.getSequenceId(), confirmEvent.getSequenceId());
        Assertions.assertEquals(EventConfirmationType.UNCONFIRMABLE, confirmEvent.getConfirmationType());
        Assertions.assertNotNull(confirmEvent.getProductData());
        Assertions.assertEquals(ITextTestEvent.class, confirmEvent.getClassFromContext());
    }
}
