package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.kernel.actions.AbstractStatisticsAggregator;
import com.itextpdf.kernel.actions.AbstractStatisticsEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    private final Map<String, AtomicLong> numberOfDocuments = new ConcurrentHashMap<>();

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
     * Retrieves Map where keys are ranges of pages and values are the amounts of such PDF documents.
     *
     * @return aggregated {@link Map}
     */
    @Override
    public Object retrieveAggregation() {
        return numberOfDocuments;
    }
}
