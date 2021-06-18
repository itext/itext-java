package com.itextpdf.kernel.actions;

/**
 * Abstract class for statistics aggregation. Note that aggregator class must be thread safe.
 */
public abstract class AbstractStatisticsAggregator {

    /**
     * Aggregates data from the provided event.
     *
     * @param event {@link AbstractStatisticsEvent} instance
     */
    public abstract void aggregate(AbstractStatisticsEvent event);

    /**
     * Retrieves aggregated data.
     *
     * @return aggregated data as {@link Object}
     */
    public abstract Object retrieveAggregation();
}
