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

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface represents the wrapper for ASN1OutputStream that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1OutputStream extends Closeable {
    /**
     * Calls actual {@code writeObject} method for the wrapped ASN1OutputStream object.
     *
     * @param primitive wrapped ASN1Primitive object.
     *
     * @throws IOException if an I/O error occurs.
     */
    void writeObject(IASN1Primitive primitive) throws IOException;

    /**
     * Delegates {@code close} method call to the wrapped stream.
     */
    @Override
    void close() throws IOException;
}
