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
package com.itextpdf.kernel.actions.data;

/**
 * Class is used to describe used product information.
 */
public final class ProductData {
    private final String publicProductName;
    private final String moduleName;
    private final String version;
    private final int sinceCopyrightYear;
    private final int toCopyrightYear;

    /**
     * Creates a new instance of product data.
     *
     * @param publicProductName is a product name
     * @param moduleName is a technical name of the addon
     * @param version is a version of the product
     * @param sinceCopyrightYear is the first year of a product development
     * @param toCopyrightYear is a last year of a product development
     */
    public ProductData(String publicProductName, String moduleName, String version, int sinceCopyrightYear,
            int toCopyrightYear) {
        this.publicProductName = publicProductName;
        this.moduleName = moduleName;
        this.version = version;
        this.sinceCopyrightYear = sinceCopyrightYear;
        this.toCopyrightYear = toCopyrightYear;
    }

    /**
     * Getter for a product name.
     *
     * @return product name
     */
    public String getPublicProductName() {
        return publicProductName;
    }

    /**
     * Getter for a technical name of the addon.
     *
     * @return name of the module
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Getter for a version of the product
     *
     * @return version of the product
     */
    public String getVersion() {
        return version;
    }

    /**
     * Getter for the first year of copyright period.
     *
     * @return the first year of copyright
     */
    public int getSinceCopyrightYear() {
        return sinceCopyrightYear;
    }

    /**
     * Getter for the last year of copyright period
     *
     * @return the last year of copyright
     */
    public int getToCopyrightYear() {
        return toCopyrightYear;
    }
}
