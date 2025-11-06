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
package com.itextpdf.signatures.validation.lotl;

/**
 * This class represents a country-specific TSL (Trusted List) location.
 * It contains the scheme territory and the TSL location URL.
 */
public final class CountrySpecificLotl {
    private final String schemeTerritory;
    private final String tslLocation;
    private final String mimeType;


    CountrySpecificLotl(String schemeTerritory, String tslLocation, String mimeType) {
        this.schemeTerritory = schemeTerritory;
        this.tslLocation = tslLocation;
        this.mimeType = mimeType;
    }

    /**
     * Creates an empty instance of {@link CountrySpecificLotl}.
     */
    CountrySpecificLotl() {
        //Empty constructor needed for deserialization.
        this.schemeTerritory = null;
        this.tslLocation = null;
        this.mimeType = null;
    }

    /**
     * Returns the scheme territory of this country-specific TSL.
     *
     * @return The scheme territory
     */
    public String getSchemeTerritory() {
        return schemeTerritory;
    }

    /**
     * Returns the TSL location URL of this country-specific TSL.
     *
     * @return The TSL location URL
     */
    public String getTslLocation() {
        return tslLocation;
    }

    /**
     * Returns the MIME type of the TSL location.
     *
     * @return The MIME type of the TSL location
     */
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String toString() {
        return "CountrySpecificLotl{" + "schemeTerritory='" +
                schemeTerritory + '\'' + ", tslLocation='" + tslLocation + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
