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

import com.itextpdf.commons.actions.AbstractStatisticsAggregator;
import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class SizeOfPdfStatisticsUnitTest extends ExtendedITextTest {
    @Test
    public void defaultEventTest() {
        SizeOfPdfStatisticsEvent event = new SizeOfPdfStatisticsEvent(0, ITextCoreProductData.getInstance());

        Assertions.assertEquals(0, event.getAmountOfBytes());
        Assertions.assertEquals(Collections.singletonList("pdfSize"), event.getStatisticsNames());
        Assertions.assertEquals(SizeOfPdfStatisticsAggregator.class, event.createStatisticsAggregatorFromName("pdfSize").getClass());
    }

    @Test
    public void invalidArgumentEventTest() {
        Exception exception =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> new SizeOfPdfStatisticsEvent(-1, ITextCoreProductData.getInstance()));
        Assertions.assertEquals(KernelExceptionMessageConstant.AMOUNT_OF_BYTES_LESS_THAN_ZERO, exception.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = CommonsLogMessageConstant.INVALID_STATISTICS_NAME))
    public void invalidStatisticsNameEventTest() {
        SizeOfPdfStatisticsEvent event = new SizeOfPdfStatisticsEvent(5, ITextCoreProductData.getInstance());
        Assertions.assertNull(event.createStatisticsAggregatorFromName("invalid name"));
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
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertEquals(4, castedAggregation.size());

        Long numberOfPages = castedAggregation.get("<128kb");
        Assertions.assertEquals(new Long(2L), numberOfPages);

        numberOfPages = castedAggregation.get("128kb-1mb");
        Assertions.assertEquals(new Long(2L), numberOfPages);

        Assertions.assertNull(castedAggregation.get("1mb-16mb"));

        numberOfPages = castedAggregation.get("16mb-128mb");
        Assertions.assertEquals(new Long(1L), numberOfPages);

        numberOfPages = castedAggregation.get("128mb+");
        Assertions.assertEquals(new Long(2L), numberOfPages);
    }

    @Test
    public void nothingAggregatedTest() {
        SizeOfPdfStatisticsAggregator aggregator = new SizeOfPdfStatisticsAggregator();
        Object aggregation = aggregator.retrieveAggregation();
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertTrue(castedAggregation.isEmpty());
    }

    @Test
    public void aggregateWrongEventTest() {
        SizeOfPdfStatisticsAggregator aggregator = new SizeOfPdfStatisticsAggregator();
        aggregator.aggregate(new NumberOfPagesStatisticsEvent(200, ITextCoreProductData.getInstance()));

        Object aggregation = aggregator.retrieveAggregation();
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertTrue(castedAggregation.isEmpty());
    }

    @Test
    public void mergeTest() {
        SizeOfPdfStatisticsAggregator aggregator1 = new SizeOfPdfStatisticsAggregator();
        SizeOfPdfStatisticsAggregator aggregator2 = new SizeOfPdfStatisticsAggregator();

        SizeOfPdfStatisticsEvent event = new SizeOfPdfStatisticsEvent(100, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(128 * 1024, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(128 * 1024 + 1, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(100000000, ITextCoreProductData.getInstance());
        aggregator1.aggregate(event);

        event = new SizeOfPdfStatisticsEvent(1024 * 1024, ITextCoreProductData.getInstance());
        aggregator2.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(167972160, ITextCoreProductData.getInstance());
        aggregator2.aggregate(event);
        event = new SizeOfPdfStatisticsEvent(999999999999L, ITextCoreProductData.getInstance());
        aggregator2.aggregate(event);

        aggregator1.merge(aggregator2);

        Object aggregation = aggregator1.retrieveAggregation();
        Map<String, Long> castedAggregation = (Map<String, Long>) aggregation;

        Assertions.assertEquals(4, castedAggregation.size());

        Long numberOfPages = castedAggregation.get("<128kb");
        Assertions.assertEquals(new Long(2L), numberOfPages);

        numberOfPages = castedAggregation.get("128kb-1mb");
        Assertions.assertEquals(new Long(2L), numberOfPages);

        Assertions.assertNull(castedAggregation.get("1mb-16mb"));

        numberOfPages = castedAggregation.get("16mb-128mb");
        Assertions.assertEquals(new Long(1L), numberOfPages);

        numberOfPages = castedAggregation.get("128mb+");
        Assertions.assertEquals(new Long(2L), numberOfPages);
    }
}
