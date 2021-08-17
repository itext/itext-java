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
package com.itextpdf.events.processors;

import com.itextpdf.events.AbstractProductProcessITextEvent;
import com.itextpdf.events.exceptions.EventsExceptionMessageConstant;

/**
 * Defines a default strategy of product event processing.
 */
public class DefaultITextProductEventProcessor implements ITextProductEventProcessor {

    private final String productName;

    /**
     * Creates an instance of product event processor.
     *
     * @param productName is a product name
     */
    public DefaultITextProductEventProcessor(String productName) {
        if (productName == null) {
            throw new IllegalArgumentException(EventsExceptionMessageConstant.PRODUCT_NAME_CAN_NOT_BE_NULL);
        }
        this.productName = productName;
    }

    @Override
    public void onEvent(AbstractProductProcessITextEvent event) {
        // TODO: DEVSIX-5341 provide appropriate logic if any
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public String getUsageType() {
        return "AGPL";
    }

    @Override
    public String getProducer() {
        return "iText\u00ae ${usedProducts:P V (T 'version')} \u00a9${copyrightSince}-${copyrightTo} iText Group NV";
    }
}
