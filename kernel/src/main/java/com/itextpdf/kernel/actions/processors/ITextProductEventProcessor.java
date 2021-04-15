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
package com.itextpdf.kernel.actions.processors;

import com.itextpdf.kernel.actions.session.ClosingSession;
import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;

/**
 * Interface for product event processors.
 */
public interface ITextProductEventProcessor {
    /**
     * Handles the {@link AbstractITextProductEvent}.
     *
     * @param event to handle
     */
    void onEvent(AbstractITextProductEvent event);

    /**
     * Gets the name of the product to which this processor corresponds.
     *
     * @return the product name
     */
    String getProductName();

    /**
     * Gets the usage type of the product to which this processor corresponds.
     *
     * @return the usage type
     */
    String getUsageType();

    /**
     * Gets the producer line for the product.
     *
     * @return the producer line
     */
    String getProducer();

    /**
     * When document is closing it will search for every {@link ITextProductEventProcessor}
     * associated with the products involved into document processing and may aggregate some data
     * from them. Aggregation stage is the first stage of closing process. See also the second step:
     * {@link ITextProductEventProcessor#completionOnClose(ClosingSession)}
     *
     * @param session is a closing session
     */
    void aggregationOnClose(ClosingSession session);

    /**
     * When document is closing it will search for every {@link ITextProductEventProcessor}
     * associated with the products involved into document processing and may aggregate some data
     * from them. Completion stage is the second stage of closing process. See also the first step:
     * {@link ITextProductEventProcessor#aggregationOnClose(ClosingSession)}
     *
     * @param session is a closing session
     */
    void completionOnClose(ClosingSession session);
}
