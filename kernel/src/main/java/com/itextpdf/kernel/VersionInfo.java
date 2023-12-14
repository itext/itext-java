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
package com.itextpdf.kernel;

import java.io.Serializable;

public class VersionInfo implements Serializable {

    private static final long serialVersionUID = 1514128839876564529L;

    private final String productName;
    private final String releaseNumber;
    private final String producerLine;
    private final String licenseKey;

    public VersionInfo(String productName, String releaseNumber, String producerLine, String licenseKey) {
        this.productName = productName;
        this.releaseNumber = releaseNumber;
        this.producerLine = producerLine;
        this.licenseKey = licenseKey;
    }

    /**
     * Gets the product name.
     * iText Group NV requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     *
     * @return the product name
     */
    public String getProduct() {
        return productName;
    }

    /**
     * Gets the release number.
     * iText Group NV requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     *
     * @return the release number
     */
    public String getRelease() {
        return releaseNumber;
    }

    /**
     * Returns the iText version as shown in the producer line.
     * iText is a product developed by iText Group NV.
     * iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     *
     * @return iText version
     */
    public String getVersion() {
        return producerLine;
    }

    /**
     * Returns a license key if one was provided, or null if not.
     *
     * @return a license key.
     */
    public String getKey() {
        return licenseKey;
    }
}
