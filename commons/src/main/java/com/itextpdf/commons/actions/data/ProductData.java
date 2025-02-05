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
package com.itextpdf.commons.actions.data;

import java.util.Objects;

/**
 * Class is used to describe used product information.
 */
public final class ProductData {
    private final String publicProductName;
    private final String productName;
    private final String version;
    private final String minimalCompatibleLicenseKeyVersion;
    private final int sinceCopyrightYear;
    private final int toCopyrightYear;

    /**
     * Creates a new instance of product data.
     *
     * @param publicProductName is a product name
     * @param productName is a technical name of the product
     * @param version is a version of the product
     * @param sinceCopyrightYear is the first year of a product development
     * @param toCopyrightYear is a last year of a product development
     */
    public ProductData(String publicProductName, String productName, String version, int sinceCopyrightYear,
            int toCopyrightYear) {
        this(publicProductName, productName, version, null, sinceCopyrightYear, toCopyrightYear);
    }

    /**
     * Creates a new instance of product data.
     *
     * @param publicProductName is a product name
     * @param productName is a technical name of the product
     * @param version is a version of the product
     * @param minimalCompatibleLicenseKeyVersion is a minimal compatible version of licensekey library
     * @param sinceCopyrightYear is the first year of a product development
     * @param toCopyrightYear is a last year of a product development
     */
    public ProductData(String publicProductName, String productName, String version,
            String minimalCompatibleLicenseKeyVersion, int sinceCopyrightYear, int toCopyrightYear) {
        this.publicProductName = publicProductName;
        this.productName = productName;
        this.version = version;
        this.minimalCompatibleLicenseKeyVersion = minimalCompatibleLicenseKeyVersion;
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
     * Getter for a technical name of the product.
     *
     * @return the technical name of the product
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Getter for a version of the product.
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
     * Getter for the last year of copyright period.
     *
     * @return the last year of copyright
     */
    public int getToCopyrightYear() {
        return toCopyrightYear;
    }

    /**
     * Getter for the minimal compatible licensekey version.
     *
     * @return minimal compatible version of licensekey library.
     */
    public String getMinCompatibleLicensingModuleVersion() {
        return minimalCompatibleLicenseKeyVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ProductData other = (ProductData) o;
        return Objects.equals(publicProductName, other.publicProductName) && Objects
                .equals(productName, other.productName) &&
                Objects.equals(version, other.version) && sinceCopyrightYear == other.sinceCopyrightYear
                && toCopyrightYear == other.toCopyrightYear;
    }

    @Override
    public int hashCode() {
        int result = publicProductName != null ? publicProductName.hashCode() : 0;
        result += 31 * result + (productName != null ? productName.hashCode() : 0);
        result += 31 * result + (version != null ? version.hashCode() : 0);
        result += 31 * result + sinceCopyrightYear;
        result += 31 * result + toCopyrightYear;
        return result;
    }
}
