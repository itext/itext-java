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
package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for DistributionPointName that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IDistributionPointName extends IASN1Encodable {
    /**
     * Calls actual {@code getType} method for the wrapped DistributionPointName object.
     *
     * @return type value.
     */
    int getType();

    /**
     * Calls actual {@code getName} method for the wrapped DistributionPointName object.
     *
     * @return {@link IASN1Encodable} ASN1Encodable wrapper.
     */
    IASN1Encodable getName();

    /**
     * Gets {@code FULL_NAME} constant for the wrapped DistributionPointName.
     *
     * @return DistributionPointName.FULL_NAME value.
     */
    int getFullName();
}
