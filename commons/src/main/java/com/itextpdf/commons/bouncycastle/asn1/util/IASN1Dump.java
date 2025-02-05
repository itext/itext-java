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
package com.itextpdf.commons.bouncycastle.asn1.util;

/**
 * This interface represents the wrapper for ASN1Dump that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Dump {
    /**
     * Calls actual {@code dumpAsString} method for the wrapped ASN1Dump object.
     *
     * @param obj the ASN1Primitive (or its wrapper) to be dumped out
     * @param b   if true, dump out the contents of octet and bit strings
     *
     * @return the resulting string.
     */
    String dumpAsString(Object obj, boolean b);

    /**
     * Calls actual {@code dumpAsString} method for the wrapped ASN1Dump object.
     *
     * @param obj the ASN1Primitive (or its wrapper) to be dumped out
     *
     * @return the resulting string.
     */
    String dumpAsString(Object obj);
}
