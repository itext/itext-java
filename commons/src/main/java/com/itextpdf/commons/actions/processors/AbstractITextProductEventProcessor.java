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
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;

/**
 * Abstract class with some standard functionality for product event processing.
 */
public abstract class AbstractITextProductEventProcessor implements ITextProductEventProcessor {

    private final String productName;

    /**
     * Creates a new instance of an abstract processor for the provided product.
     *
     * @param productName the product which will be handled by this processor
     */
    public AbstractITextProductEventProcessor(String productName) {
        if (productName == null) {
            throw new IllegalArgumentException(CommonsExceptionMessageConstant.PRODUCT_NAME_CAN_NOT_BE_NULL);
        }
        this.productName = productName;
    }

    @Override
    public abstract void onEvent(AbstractProductProcessITextEvent event);

    @Override
    public abstract String getUsageType();

    @Override
    public String getProducer() {
        return "iText\u00ae ${usedProducts:P V (T 'version')} \u00a9${copyrightSince}-${copyrightTo} Apryse Group NV";
    }

    @Override
    public String getProductName() {
        return productName;
    }
}
