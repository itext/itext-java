package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.List;

@Category(UnitTest.class)
public class AbstractStatisticsEventTest extends ExtendedITextTest {
    @Test
    public void constructorTest() {
        DummyStatisticsEvent dummyEvent =
                new DummyStatisticsEvent(new ProductData("public name", "product name", "version", 15, 3000));

        ProductData data = dummyEvent.getProductData();
        Assert.assertEquals("public name", data.getPublicProductName());
        Assert.assertEquals("product name", data.getProductName());
        Assert.assertEquals("version", data.getVersion());
        Assert.assertEquals(15, data.getSinceCopyrightYear());
        Assert.assertEquals(3000, data.getToCopyrightYear());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = CommonsLogMessageConstant.INVALID_STATISTICS_NAME))
    public void createStatisticsAggregatorFromNameTest() {
        DummyStatisticsEvent dummyEvent =
                new DummyStatisticsEvent(new ProductData("public name", "product name", "version", 15, 3000));

        Assert.assertNull(dummyEvent.createStatisticsAggregatorFromName("statisticsName"));
    }

    static class DummyStatisticsEvent extends AbstractStatisticsEvent {

        DummyStatisticsEvent(ProductData data) {
            super(data);
        }

        @Override
        public List<String> getStatisticsNames() {
            return null;
        }
    }
}
