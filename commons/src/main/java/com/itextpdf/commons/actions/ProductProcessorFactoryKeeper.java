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

import com.itextpdf.commons.actions.processors.DefaultProductProcessorFactory;
import com.itextpdf.commons.actions.processors.IProductProcessorFactory;

/**
 * Helper class which allow to change used product processor factory instance.
 */
final class ProductProcessorFactoryKeeper {
    private static final IProductProcessorFactory DEFAULT_FACTORY = new DefaultProductProcessorFactory();
    private static IProductProcessorFactory productProcessorFactory = DEFAULT_FACTORY;

    private ProductProcessorFactoryKeeper() {
        // do nothing
    }

    /**
     * Sets product processor factory instance.
     *
     * @param productProcessorFactory the instance to be set
     */
    static void setProductProcessorFactory(IProductProcessorFactory productProcessorFactory) {
        ProductProcessorFactoryKeeper.productProcessorFactory = productProcessorFactory;
    }

    /**
     * Restores default factory.
     */
    static void restoreDefaultProductProcessorFactory() {
        ProductProcessorFactoryKeeper.productProcessorFactory = DEFAULT_FACTORY;
    }

    /**
     * Gets reporting product processor factory instance.
     *
     * @return the product processor factory instance
     */
    static IProductProcessorFactory getProductProcessorFactory() {
        return productProcessorFactory;
    }
}
