/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class NumberOfPagesStatisticsUnitTest extends ExtendedITextTest {

    @Test
    public void defaultEventTest() {
        NumberOfPagesStatisticsEvent event = new NumberOfPagesStatisticsEvent(1, ITextCoreProductData.getInstance());

        Assertions.assertEquals(1, event.getNumberOfPages());
        Assertions.assertEquals(Collections.singletonList("numberOfPages"), event.getStatisticsNames());
        Assertions.assertEquals(NumberOfPagesStatisticsAggregator.class, event.createStatisticsAggregatorFromName("numberOfPages").getClass());
    }

    @Test
    public void invalidArgumentEventTest() {
        Exception exception =
                Assertions.assertThrows(
                        IllegalStateException.class,
                        () -> new NumberOfPagesStatisticsEvent(-1, ITextCoreProductData.getInstance()));
        Assertions.assertEquals(KernelExceptionMessageConstant.NUMBER_OF_PAGES_CAN_NOT_BE_NEGATIVE, exception.getMessage());
    }

    @Test
    public void zeroNumberOfPagesTest() {
        AssertUtil.doesNotThrow(() -> new NumberOfPagesStatisticsEvent(0, ITextCoreProductData.getInstance()));
    }

    @Test
    public void aggregateZeroPageEventTest() {
        NumberOfPagesStatisticsAggregator aggregator = new NumberOfPagesStatisticsAggregator();
        aggregator.aggregate(new NumberOfPagesStatisticsEvent(0, ITextCoreProductData.getInstance()));

        Object aggregation = aggregator.retrieveAggregation();
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertEquals(1, castedAggregation.size());

        Long numberOfPages = castedAggregation.get("1");
        Assertions.assertEquals(new Long(1L), numberOfPages);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = CommonsLogMessageConstant.INVALID_STATISTICS_NAME))
    public void invalidStatisticsNameEventTest() {
        NumberOfPagesStatisticsEvent event = new NumberOfPagesStatisticsEvent(5, ITextCoreProductData.getInstance());
        Assertions.assertNull(event.createStatisticsAggregatorFromName("invalid name"));
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
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertEquals(4, castedAggregation.size());

        Long numberOfPages = castedAggregation.get("1");
        Assertions.assertEquals(new Long(1L), numberOfPages);

        numberOfPages = castedAggregation.get("2-10");
        Assertions.assertEquals(new Long(4L), numberOfPages);

        Assertions.assertNull(castedAggregation.get("11-100"));

        numberOfPages = castedAggregation.get("101-1000");
        Assertions.assertEquals(new Long(2L), numberOfPages);

        numberOfPages = castedAggregation.get("1001+");
        Assertions.assertEquals(new Long(1L), numberOfPages);
    }

    @Test
    public void nothingAggregatedTest() {
        NumberOfPagesStatisticsAggregator aggregator = new NumberOfPagesStatisticsAggregator();
        Object aggregation = aggregator.retrieveAggregation();
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertTrue(castedAggregation.isEmpty());
    }

    @Test
    public void aggregateWrongEventTest() {
        NumberOfPagesStatisticsAggregator aggregator = new NumberOfPagesStatisticsAggregator();
        aggregator.aggregate(new SizeOfPdfStatisticsEvent(200, ITextCoreProductData.getInstance()));

        Object aggregation = aggregator.retrieveAggregation();
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertTrue(castedAggregation.isEmpty());
    }

    @Test
    public void mergeTest() {
        NumberOfPagesStatisticsAggregator aggregator1 = new NumberOfPagesStatisticsAggregator();
        NumberOfPagesStatisticsAggregator aggregator2 = new NumberOfPagesStatisticsAggregator();

        NumberOfPagesStatisticsEvent event = new NumberOfPagesStatisticsEvent(5, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(1, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(7, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(10, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(1000, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);

        event = new NumberOfPagesStatisticsEvent(500, ITextCoreProductData.getInstance());
        aggregator2.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(100000000, ITextCoreProductData.getInstance());
        aggregator2.aggregate(event);
        event = new NumberOfPagesStatisticsEvent(2, ITextCoreProductData.getInstance());
        aggregator2.aggregate(event);

        aggregator1.merge(aggregator2);

        Object aggregation = aggregator1.retrieveAggregation();
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertEquals(4, castedAggregation.size());

        Long numberOfPages = castedAggregation.get("1");
        Assertions.assertEquals(new Long(1L), numberOfPages);

        numberOfPages = castedAggregation.get("2-10");
        Assertions.assertEquals(new Long(4L), numberOfPages);

        Assertions.assertNull(castedAggregation.get("11-100"));

        numberOfPages = castedAggregation.get("101-1000");
        Assertions.assertEquals(new Long(2L), numberOfPages);

        numberOfPages = castedAggregation.get("1001+");
        Assertions.assertEquals(new Long(1L), numberOfPages);
    }
}
