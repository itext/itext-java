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

/**
 * Abstract class which defines general product events by encapsulating
 * {@link ProductData} of the product which generated event. Only for internal usage.
 */
public abstract class AbstractProductITextEvent extends AbstractITextEvent {
    private final ProductData productData;

    /**
     * Creates instance of abstract product iText event based
     * on passed product data. Only for internal usage.
     *
     * @param productData is a description of the product which has generated an event
     */
    protected AbstractProductITextEvent(ProductData productData) {
        super();
        if (productData == null) {
            // IllegalStateException is thrown because AbstractProductITextEvent for internal usage
            throw new IllegalStateException("ProductData shouldn't be null.");
        }
        this.productData = productData;
    }

    /**
     * Gets a product data which generated the event.
     *
     * @return information about the product
     */
    public ProductData getProductData() {
        return productData;
    }

    /**
     * Gets a name of product which generated the event.
     *
     * @return product name
     */
    public String getProductName() {
        return getProductData().getProductName();
    }
}
