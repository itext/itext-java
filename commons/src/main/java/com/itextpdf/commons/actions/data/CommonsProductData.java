/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.commons.actions.data;

/**
 * Stores an instance of {@link ProductData} related to iText commons module.
 */
public final class CommonsProductData {
    static final String COMMONS_PUBLIC_PRODUCT_NAME = "Commons";
    static final String COMMONS_PRODUCT_NAME = "commons";
    static final String COMMONS_VERSION = "8.0.2-SNAPSHOT";
    static final String MINIMAL_COMPATIBLE_LICENSEKEY_VERSION = "4.1.0";
    static final int COMMONS_COPYRIGHT_SINCE = 2000;
    static final int COMMONS_COPYRIGHT_TO = 2023;

    private static final ProductData COMMONS_PRODUCT_DATA = new ProductData(COMMONS_PUBLIC_PRODUCT_NAME,
            COMMONS_PRODUCT_NAME, COMMONS_VERSION, MINIMAL_COMPATIBLE_LICENSEKEY_VERSION, COMMONS_COPYRIGHT_SINCE,
            COMMONS_COPYRIGHT_TO);

    private CommonsProductData() {
        // Empty constructor for util class
    }

    /**
     * Getter for an instance of {@link ProductData} related to iText commons module.
     *
     * @return iText commons product description
     */
    public static ProductData getInstance() {
        return COMMONS_PRODUCT_DATA;
    }
}
