package com.itextpdf.kernel.actions;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.KernelLogMessageConstant;
import com.itextpdf.kernel.actions.data.ProductData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Abstract class which defines statistics event. Only for internal usage.
 */
public abstract class AbstractStatisticsEvent extends AbstractProductITextEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatisticsEvent.class);

    /**
     * @see AbstractProductITextEvent#AbstractProductITextEvent(ProductData)
     */
    protected AbstractStatisticsEvent(ProductData productData) {
        super(productData);
    }

    /**
     * Creates statistics aggregator based on provided statistics name.
     * By default prints log warning and returns <code>null</code>.
     *
     * @param statisticsName name of statistics based on which aggregator will be created.
     *                       Shall be one of those returned from {@link this#getStatisticsNames()}
     * @return new instance of {@link AbstractStatisticsAggregator}
     */
    public AbstractStatisticsAggregator createStatisticsAggregatorFromName(String statisticsName) {
        LOGGER.warn(MessageFormatUtil.format(KernelLogMessageConstant.INVALID_STATISTICS_NAME, statisticsName));
        return null;
    }

    /**
     * Gets all statistics names related to this event.
     *
     * @return {@link List} of statistics names
     */
    public abstract List<String> getStatisticsNames();
}
