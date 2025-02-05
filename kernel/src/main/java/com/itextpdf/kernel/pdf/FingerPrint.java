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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.actions.data.ProductData;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Data container for debugging information. This class keeps a record of every registered product that
 * was involved in the creation of a certain PDF file.
 */
public class FingerPrint {

    private Set<ProductData> productDataSet;

    private boolean fingerPrintEnabled = true;

    /**
     * Default constructor. Initializes the productDataSet.
     */
    public FingerPrint() {
        this.productDataSet = new LinkedHashSet<>();
    }

    /**
     * This method is used to disable iText fingerprint.
     * IText fingerPrint can only be disabled if all products are in non AGPL mode.
     *
     */
    public void disableFingerPrint() {
        fingerPrintEnabled = false;
    }

    /**
     * This method is used to check iText fingerprint state.
     *
     * @return true if fingerprint will be added to the document
     */
    public boolean isFingerPrintEnabled() {
        return fingerPrintEnabled;
    }

    /**
     * Registers a product to be added to the fingerprint or other debugging info.
     *
     * @param productData ProductData to be added
     * @return true if the fingerprint did not already contain the specified element
     */
    public boolean registerProduct(final ProductData productData) {
        int initialSize = productDataSet.size();
        productDataSet.add(productData);
        return initialSize != productDataSet.size();
    }

    /**
     * Returns the registered products.
     *
     * @return registered products.
     */
    public Collection<ProductData> getProducts() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(this.productDataSet));
    }

}
