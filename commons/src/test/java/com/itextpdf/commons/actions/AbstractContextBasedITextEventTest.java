package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.data.CommonsProductData;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.ecosystem.TestMetaInfo;
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AbstractContextBasedITextEventTest extends ExtendedITextTest {

    @Test
    public void setMetaInfoTest() {
        BasicAbstractContextBasedITextEvent e =
                new BasicAbstractContextBasedITextEvent(CommonsProductData.getInstance(), null);

        TestMetaInfo metaInfoAfter = new TestMetaInfo("meta-info-after");
        e.setMetaInfo(metaInfoAfter);
        Assert.assertSame(metaInfoAfter, e.getMetaInfo());
    }

    @Test
    public void resetMetaInfoForbiddenTest() {
        TestMetaInfo metaInfoBefore = new TestMetaInfo("meta-info-before");
        TestMetaInfo metaInfoAfter = new TestMetaInfo("meta-info-after");
        BasicAbstractContextBasedITextEvent e =
                new BasicAbstractContextBasedITextEvent(CommonsProductData.getInstance(), metaInfoBefore);

        Assert.assertSame(metaInfoBefore, e.getMetaInfo());

        Exception exception = Assert.assertThrows(IllegalStateException.class, () -> e.setMetaInfo(metaInfoAfter));
        Assert.assertEquals(CommonsExceptionMessageConstant.META_INFO_SHOULDNT_BE_NULL, exception.getMessage());
    }

    private static class BasicAbstractContextBasedITextEvent extends AbstractContextBasedITextEvent {
        protected BasicAbstractContextBasedITextEvent(ProductData productData,
                IMetaInfo metaInfo) {
            super(productData, metaInfo);
        }
    }
}
