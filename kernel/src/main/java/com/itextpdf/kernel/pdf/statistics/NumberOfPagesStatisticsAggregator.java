package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.kernel.actions.AbstractStatisticsAggregator;
import com.itextpdf.kernel.actions.AbstractStatisticsEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics aggregator which aggregates number of pages in PDF documents.
 */
public class NumberOfPagesStatisticsAggregator extends AbstractStatisticsAggregator {

    private static final int ONE = 1;
    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int THOUSAND = 1000;

    private static final String STRING_FOR_ONE_PAGE = "1";
    private static final String STRING_FOR_TEN_PAGES = "2-10";
    private static final String STRING_FOR_HUNDRED_PAGES = "11-100";
    private static final String STRING_FOR_THOUSAND_PAGES = "101-1000";
    private static final String STRING_FOR_INF = "1001+";

    private static final Map<Integer, String> NUMBERS_OF_PAGES;

    // This List must be sorted.
    private static final List<Integer> SORTED_UPPER_BOUNDS_OF_PAGES = Arrays.asList(ONE, TEN, HUNDRED, THOUSAND);

    static {
        Map<Integer, String> temp = new HashMap<>();
        temp.put(ONE, STRING_FOR_ONE_PAGE);
        temp.put(TEN, STRING_FOR_TEN_PAGES);
        temp.put(HUNDRED, STRING_FOR_HUNDRED_PAGES);
        temp.put(THOUSAND, STRING_FOR_THOUSAND_PAGES);
        NUMBERS_OF_PAGES = Collections.unmodifiableMap(temp);
    }

    private final Object lock = new Object();

    private final Map<String, AtomicLong> numberOfDocuments = new ConcurrentHashMap<>();

    /**
     * Aggregates number of pages from the provided event.
     *
     * @param event {@link NumberOfPagesStatisticsEvent} instance
     */
    @Override
    public void aggregate(AbstractStatisticsEvent event) {
        if (!(event instanceof NumberOfPagesStatisticsEvent)) {
            return;
        }
        int numberOfPages = ((NumberOfPagesStatisticsEvent) event).getNumberOfPages();
        String range = STRING_FOR_INF;
        for (final int upperBound : SORTED_UPPER_BOUNDS_OF_PAGES) {
            if (numberOfPages <= upperBound) {
                range = NUMBERS_OF_PAGES.get(upperBound);
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
