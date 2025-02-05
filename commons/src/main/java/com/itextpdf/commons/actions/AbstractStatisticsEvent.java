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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class which defines statistics event. Only for internal usage.
 */
public abstract class AbstractStatisticsEvent extends AbstractProductITextEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatisticsEvent.class);

    /**
     * Creates instance of abstract statistics iText event based on passed product data. Only for internal usage.
     *
     * @param productData is a description of the product which has generated an event
     */
    protected AbstractStatisticsEvent(ProductData productData) {
        super(productData);
    }

    /**
     * Creates statistics aggregator based on provided statistics name.
     * By default prints log warning and returns <code>null</code>.
     *
     * @param statisticsName name of statistics based on which aggregator will be created.
     *                       Shall be one of those returned from {@link AbstractStatisticsEvent#getStatisticsNames()}
     * @return new instance of {@link AbstractStatisticsAggregator}
     */
    public AbstractStatisticsAggregator createStatisticsAggregatorFromName(String statisticsName) {
        LOGGER.warn(MessageFormatUtil.format(CommonsLogMessageConstant.INVALID_STATISTICS_NAME, statisticsName));
        return null;
    }

    /**
     * Gets all statistics names related to this event.
     *
     * @return {@link List} of statistics names
     */
    public abstract List<String> getStatisticsNames();
}
