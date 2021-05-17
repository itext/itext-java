package com.itextpdf.kernel.actions.events;

import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.actions.ecosystem.TestMetaInfo;
import com.itextpdf.kernel.actions.sequence.SequenceId;
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
