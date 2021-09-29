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
package com.itextpdf.commons.actions.confirmations;

import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.commons.ecosystem.TestMetaInfo;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ConfirmEventTest extends ExtendedITextTest {
    @Test
    public void constructorWithSequenceIdTest() {
        SequenceId sequenceId = new SequenceId();
        ITextTestEvent iTextTestEvent = new ITextTestEvent(new SequenceId(), new TestMetaInfo(""), "eventType", "productName");
        ConfirmEvent confirmEvent = new ConfirmEvent(sequenceId, iTextTestEvent);

        Assert.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());
        Assert.assertEquals("eventType", confirmEvent.getEventType());
        Assert.assertEquals("productName", confirmEvent.getProductName());
        Assert.assertSame(sequenceId, confirmEvent.getSequenceId());
        Assert.assertEquals(EventConfirmationType.UNCONFIRMABLE, confirmEvent.getConfirmationType());
        Assert.assertNotNull(confirmEvent.getProductData());
        Assert.assertEquals(ITextTestEvent.class, confirmEvent.getClassFromContext());
    }

    @Test
    public void constructorWithoutSequenceIdTest() {
        ITextTestEvent iTextTestEvent = new ITextTestEvent(new SequenceId(), new TestMetaInfo(""), "eventType", "productName");
        ConfirmEvent confirmEvent = new ConfirmEvent(iTextTestEvent);

        Assert.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());
        Assert.assertEquals("eventType", confirmEvent.getEventType());
        Assert.assertEquals("productName", confirmEvent.getProductName());
        Assert.assertSame(iTextTestEvent.getSequenceId(), confirmEvent.getSequenceId());
        Assert.assertEquals(EventConfirmationType.UNCONFIRMABLE, confirmEvent.getConfirmationType());
        Assert.assertNotNull(confirmEvent.getProductData());
        Assert.assertEquals(ITextTestEvent.class, confirmEvent.getClassFromContext());
    }

    @Test
    public void confirmEventInsideOtherConfirmEventTest() {
        ITextTestEvent iTextTestEvent = new ITextTestEvent(new SequenceId(), new TestMetaInfo(""), "eventType", "productName");
        ConfirmEvent child = new ConfirmEvent(iTextTestEvent.getSequenceId(), iTextTestEvent);
        ConfirmEvent confirmEvent = new ConfirmEvent(child);

        Assert.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());

        Assert.assertSame(iTextTestEvent, confirmEvent.getConfirmedEvent());
        Assert.assertEquals("eventType", confirmEvent.getEventType());
        Assert.assertEquals("productName", confirmEvent.getProductName());
        Assert.assertSame(iTextTestEvent.getSequenceId(), confirmEvent.getSequenceId());
        Assert.assertEquals(EventConfirmationType.UNCONFIRMABLE, confirmEvent.getConfirmationType());
        Assert.assertNotNull(confirmEvent.getProductData());
        Assert.assertEquals(ITextTestEvent.class, confirmEvent.getClassFromContext());
    }
}
