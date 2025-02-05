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

import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.AbstractStatisticsAggregator;
import com.itextpdf.commons.actions.AbstractStatisticsEvent;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

import java.util.Collections;
import java.util.List;

/**
 * Class which represents event for counting the number of pages in a PDF document. Only for internal usage.
 */
public class NumberOfPagesStatisticsEvent extends AbstractStatisticsEvent {

    private static final String NUMBER_OF_PAGES_STATISTICS = "numberOfPages";

    private final int numberOfPages;

    /**
     * Creates an instance of this class based on the {@link ProductData} and the number of pages.
     *
     * @param numberOfPages the number of pages in the PDF document during the processing of which the event was sent
     * @param productData is a description of the product which has generated an event
     */
    public NumberOfPagesStatisticsEvent(int numberOfPages, ProductData productData) {
        super(productData);
        if (numberOfPages < 0) {
            throw new IllegalStateException(KernelExceptionMessageConstant.NUMBER_OF_PAGES_CAN_NOT_BE_NEGATIVE);
        }
        this.numberOfPages = numberOfPages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractStatisticsAggregator createStatisticsAggregatorFromName(String statisticsName) {
        if (NUMBER_OF_PAGES_STATISTICS.equals(statisticsName)) {
            return new NumberOfPagesStatisticsAggregator();
        }
        return super.createStatisticsAggregatorFromName(statisticsName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getStatisticsNames() {
        return Collections.singletonList(NUMBER_OF_PAGES_STATISTICS);
    }

    /**
     * Gets number of pages in the PDF document during the processing of which the event was sent.
     *
     * @return the number of pages
     */
    public int getNumberOfPages() {
        return numberOfPages;
    }
}
