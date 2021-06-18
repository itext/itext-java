package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.kernel.KernelLogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Category(UnitTest.class)
public class NumberOfPagesStatisticsUnitTest extends ExtendedITextTest {

    @Test
    public void defaultEventTest() {
        NumberOfPagesStatisticsEvent event = new NumberOfPagesStatisticsEvent(1, ITextCoreProductData.getInstance());

        Assert.assertEquals(1, event.getNumberOfPages());
        Assert.assertEquals(Collections.singletonList("numberOfPages"), event.getStatisticsNames());
        Assert.assertEquals(NumberOfPagesStatisticsAggregator.class, event.createStatisticsAggregatorFromName("numberOfPages").getClass());
    }

    @Test
    public void invalidArgumentEventTest() {
        Exception exception =
                Assert.assertThrows(PdfException.class, () -> new NumberOfPagesStatisticsEvent(0, ITextCoreProductData.getInstance()));
        Assert.assertEquals(PdfException.DocumentHasNoPages, exception.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.INVALID_STATISTICS_NAME))
    public void invalidStatisticsNameEventTest() {
        NumberOfPagesStatisticsEvent event = new NumberOfPagesStatisticsEvent(5, ITextCoreProductData.getInstance());
        Assert.assertNull(event.createStatisticsAggregatorFromName("invalid name"));
    }

    @Test
    public void aggregateEventTest() {
        NumberOfPagesStatisticsAggregator aggregator = new NumberOfPagesStatisticsAggregator();

        NumberOfPagesStatisticsEvent event = new NumberOfPagesStatisticsEvent(5, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(7, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(10, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(2, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(1000, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(500, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(100000000, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(1, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);

        Object aggregation = aggregator.retrieveAggregation();
        Assert.assertTrue(aggregation instanceof Map);
        Map<String, AtomicLong> castedAggregation = (Map<String, AtomicLong>) aggregation;

        Assert.assertEquals(4, castedAggregation.size());

        long numberOfPages = castedAggregation.get("1").get();
        Assert.assertEquals(1, numberOfPages);

        numberOfPages = castedAggregation.get("2-10").get();
        Assert.assertEquals(4, numberOfPages);

        Assert.assertNull(castedAggregation.get("11-100"));

        numberOfPages = castedAggregation.get("101-1000").get();
        Assert.assertEquals(2, numberOfPages);

        numberOfPages = castedAggregation.get("1001+").get();
        Assert.assertEquals(1, numberOfPages);
    }

    @Test
    public void nothingAggregatedTest() {
        NumberOfPagesStatisticsAggregator aggregator = new NumberOfPagesStatisticsAggregator();
        Object aggregation = aggregator.retrieveAggregation();
        Assert.assertTrue(aggregation instanceof Map);
        Map<String, AtomicLong> castedAggregation = (Map<String, AtomicLong>) aggregation;

        Assert.assertTrue(castedAggregation.isEmpty());
    }

    @Test
    public void aggregateWrongEventTest() {
        NumberOfPagesStatisticsAggregator aggregator = new NumberOfPagesStatisticsAggregator();
        aggregator.aggregate(new SizeOfPdfStatisticsEvent(200, ITextCoreProductData.getInstance()));

        Object aggregation = aggregator.retrieveAggregation();
        Assert.assertTrue(aggregation instanceof Map);
        Map<String, AtomicLong> castedAggregation = (Map<String, AtomicLong>) aggregation;

        Assert.assertTrue(castedAggregation.isEmpty());
    }
}
