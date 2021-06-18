package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.actions.AbstractStatisticsEvent;
import com.itextpdf.kernel.actions.AbstractStatisticsAggregator;
import com.itextpdf.kernel.actions.data.ProductData;

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
        if (numberOfPages <= 0) {
            throw new PdfException(PdfException.DocumentHasNoPages);
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
