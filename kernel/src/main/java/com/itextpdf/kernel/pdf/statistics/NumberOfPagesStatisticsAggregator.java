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
import com.itextpdf.commons.actions.AbstractStatisticsEvent;
import com.itextpdf.commons.utils.MapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private final Map<String, Long> numberOfDocuments = new LinkedHashMap<>();

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
            Long documentsOfThisRange = numberOfDocuments.get(range);
            Long currentValue = documentsOfThisRange == null ? 1L : (documentsOfThisRange.longValue() + 1L);
            numberOfDocuments.put(range, currentValue);
        }
    }

    /**
     * Retrieves Map where keys are ranges of pages and values are the amounts of such PDF documents.
     *
     * @return aggregated {@link Map}
     */
    @Override
    public Object retrieveAggregation() {
        return Collections.unmodifiableMap(numberOfDocuments);
    }

    /**
     * Merges data about amounts of ranges of pages from the provided aggregator into this aggregator.
     *
     * @param aggregator {@link NumberOfPagesStatisticsAggregator} from which data will be taken.
     */
    @Override
    public void merge(AbstractStatisticsAggregator aggregator) {
        if (!(aggregator instanceof NumberOfPagesStatisticsAggregator)) {
            return;
        }

        Map<String, Long> numberOfDocuments = ((NumberOfPagesStatisticsAggregator) aggregator).numberOfDocuments;
        synchronized (lock) {
            MapUtil.merge(this.numberOfDocuments, numberOfDocuments, (el1, el2) -> {
                if (el2 == null) {
                    return el1;
                } else {
                    return el1 + el2;
                }
            });
        }
    }
}
