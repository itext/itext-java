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
package com.itextpdf.commons.actions.processors;

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;

/**
 * Interface for product event processors.
 */
public interface ITextProductEventProcessor {
    /**
     * Handles the {@link AbstractProductProcessITextEvent}.
     *
     * @param event to handle
     */
    void onEvent(AbstractProductProcessITextEvent event);

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
}
