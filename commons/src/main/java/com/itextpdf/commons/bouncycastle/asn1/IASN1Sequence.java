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
package com.itextpdf.commons.bouncycastle.asn1;

import java.util.Enumeration;

/**
 * This interface represents the wrapper for ASN1Sequence that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Sequence extends IASN1Primitive {
    /**
     * Calls actual {@code getObjectAt} method for the wrapped ASN1Sequence object.
     *
     * @param i index
     *
     * @return {@link IASN1Encodable} wrapped ASN1Encodable object.
     */
    IASN1Encodable getObjectAt(int i);

    /**
     * Calls actual {@code getObjects} method for the wrapped ASN1Sequence object.
     *
     * @return received objects.
     */
    Enumeration getObjects();

    /**
     * Calls actual {@code size} method for the wrapped ASN1Sequence object.
     *
     * @return sequence size.
     */
    int size();

    /**
     * Calls actual {@code toArray} method for the wrapped ASN1Sequence object.
     *
     * @return array of wrapped ASN1Encodable objects.
     */
    IASN1Encodable[] toArray();
}
