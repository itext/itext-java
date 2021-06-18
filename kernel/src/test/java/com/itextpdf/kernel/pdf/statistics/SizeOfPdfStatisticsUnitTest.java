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
public class SizeOfPdfStatisticsUnitTest extends ExtendedITextTest {
    @Test
    public void defaultEventTest() {
        SizeOfPdfStatisticsEvent event = new SizeOfPdfStatisticsEvent(0, ITextCoreProductData.getInstance());

        Assert.assertEquals(0, event.getAmountOfBytes());
        Assert.assertEquals(Collections.singletonList("pdfSize"), event.getStatisticsNames());
        Assert.assertEquals(SizeOfPdfStatisticsAggregator.class, event.createStatisticsAggregatorFromName("pdfSize").getClass());
    }

    @Test
    public void invalidArgumentEventTest() {
        Exception exception =
                Assert.assertThrows(IllegalArgumentException.class,
                        () -> new SizeOfPdfStatisticsEvent(-1, ITextCoreProductData.getInstance()));
        Assert.assertEquals(PdfException.AmountOfBytesLessThanZero, exception.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.INVALID_STATISTICS_NAME))
    public void invalidStatisticsNameEventTest() {
        SizeOfPdfStatisticsEvent event = new SizeOfPdfStatisticsEvent(5, ITextCoreProductData.getInstance());
        Assert.assertNull(event.createStatisticsAggregatorFromName("invalid name"));
    }

    @Test
    public void aggregateEventTest() {
        SizeOfPdfStatisticsAggregator aggregator = new SizeOfPdfStatisticsAggregator();

        SizeOfPdfStatisticsEvent event = new SizeOfPdfStatisticsEvent(100, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(128 * 1024, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(128 * 1024 + 1, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(1024 * 1024, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(100000000, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(167972160, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(999999999999L, ITextCoreProductData.getInstance());
        aggregator.aggregate(event);

        Object aggregation = aggregator.retrieveAggregation();
        Assert.assertTrue(aggregation instanceof Map);
        Map<String, AtomicLong> castedAggregation = (Map<String, AtomicLong>) aggregation;

        Assert.assertEquals(4, castedAggregation.size());

        long numberOfPages = castedAggregation.get("<128kb").get();
        Assert.assertEquals(2, numberOfPages);

        numberOfPages = castedAggregation.get("128kb-1mb").get();
        Assert.assertEquals(2, numberOfPages);

        Assert.assertNull(castedAggregation.get("1mb-16mb"));

        numberOfPages = castedAggregation.get("16mb-128mb").get();
        Assert.assertEquals(1, numberOfPages);

        numberOfPages = castedAggregation.get("128mb+").get();
        Assert.assertEquals(2, numberOfPages);
    }

    @Test
    public void nothingAggregatedTest() {
        SizeOfPdfStatisticsAggregator aggregator = new SizeOfPdfStatisticsAggregator();
        Object aggregation = aggregator.retrieveAggregation();
        Assert.assertTrue(aggregation instanceof Map);
        Map<String, AtomicLong> castedAggregation = (Map<String, AtomicLong>) aggregation;

        Assert.assertTrue(castedAggregation.isEmpty());
    }

    @Test
    public void aggregateWrongEventTest() {
        SizeOfPdfStatisticsAggregator aggregator = new SizeOfPdfStatisticsAggregator();
        aggregator.aggregate(new NumberOfPagesStatisticsEvent(200, ITextCoreProductData.getInstance()));

        Object aggregation = aggregator.retrieveAggregation();
        Assert.assertTrue(aggregation instanceof Map);
        Map<String, AtomicLong> castedAggregation = (Map<String, AtomicLong>) aggregation;

        Assert.assertTrue(castedAggregation.isEmpty());
    }
}
