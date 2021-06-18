package com.itextpdf.kernel.pdf.statistics;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.actions.AbstractStatisticsAggregator;
import com.itextpdf.kernel.actions.AbstractStatisticsEvent;
import com.itextpdf.kernel.actions.data.ProductData;

import java.util.Collections;
import java.util.List;

/**
 * Class which represents event related to size of the PDF document. Only for internal usage.
 */
public class SizeOfPdfStatisticsEvent extends AbstractStatisticsEvent {

    private static final String PDF_SIZE_STATISTICS = "pdfSize";

    private final long amountOfBytes;

    /**
     * Creates an instance of this class based on the {@link ProductData} and the size of the document.
     *
     * @param amountOfBytes the number of bytes in the PDF document during the processing of which the event was sent
     * @param productData is a description of the product which has generated an event
     */
    public SizeOfPdfStatisticsEvent(long amountOfBytes, ProductData productData) {
        super(productData);
        if (amountOfBytes < 0) {
            throw new IllegalArgumentException(PdfException.AmountOfBytesLessThanZero);
        }
        this.amountOfBytes = amountOfBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractStatisticsAggregator createStatisticsAggregatorFromName(String statisticsName) {
        if (PDF_SIZE_STATISTICS.equals(statisticsName)) {
            return new SizeOfPdfStatisticsAggregator();
        }
        return super.createStatisticsAggregatorFromName(statisticsName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getStatisticsNames() {
        return Collections.singletonList(PDF_SIZE_STATISTICS);
    }

    /**
     * Gets number of bytes in the PDF document during the processing of which the event was sent.
     *
     * @return the number of pages
     */
    public long getAmountOfBytes() {
        return amountOfBytes;
    }
}
