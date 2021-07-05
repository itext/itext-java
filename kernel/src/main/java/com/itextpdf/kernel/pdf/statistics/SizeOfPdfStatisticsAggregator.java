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
package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.io.util.MapUtil;
import com.itextpdf.kernel.actions.AbstractStatisticsAggregator;
import com.itextpdf.kernel.actions.AbstractStatisticsEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics aggregator which aggregates size of PDF documents.
 */
public class SizeOfPdfStatisticsAggregator extends AbstractStatisticsAggregator {

    private static final long MEASURE_COEFFICIENT = 1024;

    private static final long SIZE_128KB = 128 * MEASURE_COEFFICIENT;
    private static final long SIZE_1MB = MEASURE_COEFFICIENT * MEASURE_COEFFICIENT;
    private static final long SIZE_16MB = 16 * MEASURE_COEFFICIENT * MEASURE_COEFFICIENT;
    private static final long SIZE_128MB = 128 * MEASURE_COEFFICIENT * MEASURE_COEFFICIENT;

    private static final String STRING_FOR_128KB = "<128kb";
    private static final String STRING_FOR_1MB = "128kb-1mb";
    private static final String STRING_FOR_16MB = "1mb-16mb";
    private static final String STRING_FOR_128MB = "16mb-128mb";
    private static final String STRING_FOR_INF = "128mb+";

    private static final Map<Long, String> DOCUMENT_SIZES;

    // This List must be sorted.
    private static final List<Long> SORTED_UPPER_BOUNDS_OF_SIZES =
            Arrays.asList(SIZE_128KB, SIZE_1MB, SIZE_16MB, SIZE_128MB);

    static {
        Map<Long, String> temp = new HashMap<>();
        temp.put(SIZE_128KB, STRING_FOR_128KB);
        temp.put(SIZE_1MB, STRING_FOR_1MB);
        temp.put(SIZE_16MB, STRING_FOR_16MB);
        temp.put(SIZE_128MB, STRING_FOR_128MB);
        DOCUMENT_SIZES = Collections.unmodifiableMap(temp);
    }

    private final Object lock = new Object();

    private final Map<String, AtomicLong> numberOfDocuments = new LinkedHashMap<>();

    /**
     * Aggregates size of the PDF document from the provided event.
     *
     * @param event {@link SizeOfPdfStatisticsEvent} instance
     */
    @Override
    public void aggregate(AbstractStatisticsEvent event) {
        if (!(event instanceof SizeOfPdfStatisticsEvent)) {
            return;
        }
        long sizeOfPdf = ((SizeOfPdfStatisticsEvent) event).getAmountOfBytes();
        String range = STRING_FOR_INF;
        for (final long upperBound : SORTED_UPPER_BOUNDS_OF_SIZES) {
            if (sizeOfPdf <= upperBound) {
                range = DOCUMENT_SIZES.get(upperBound);
                break;
            }
        }
        synchronized (lock) {
            AtomicLong documentsOfThisRange = numberOfDocuments.get(range);
            if (documentsOfThisRange == null) {
                numberOfDocuments.put(range, new AtomicLong(1));
            } else {
                documentsOfThisRange.incrementAndGet();
            }
        }
    }

    /**
     * Retrieves Map where keys are ranges of document sizes and values are the amounts of such PDF documents.
     *
     * @return aggregated {@link Map}
     */
    @Override
    public Object retrieveAggregation() {
        return Collections.unmodifiableMap(numberOfDocuments);
    }

    /**
     * Merges data about amounts of ranges of document sizes from the provided aggregator into this aggregator.
     *
     * @param aggregator {@link SizeOfPdfStatisticsAggregator} from which data will be taken.
     */
    @Override
    public void merge(AbstractStatisticsAggregator aggregator) {
        if (!(aggregator instanceof SizeOfPdfStatisticsAggregator)) {
            return;
        }

        Map<String, AtomicLong> numberOfDocuments = ((SizeOfPdfStatisticsAggregator) aggregator).numberOfDocuments;
        synchronized (lock) {
            MapUtil.merge(this.numberOfDocuments, numberOfDocuments, (el1, el2) -> {
                el1.addAndGet(el2.get());
                return el1;
            });
        }
    }
}
