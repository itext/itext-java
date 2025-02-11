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
package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;

import org.bouncycastle.asn1.DEROctetString;

/**
 * Wrapper class for {@link DEROctetString}.
 */
public class DEROctetStringBC extends ASN1OctetStringBC implements IDEROctetString {
    /**
     * Creates new wrapper instance for {@link DEROctetString}.
     *
     * @param bytes byte array to create {@link DEROctetString} to be wrapped
     */
    public DEROctetStringBC(byte[] bytes) {
        super(new DEROctetString(bytes));
    }

    /**
     * Creates new wrapper instance for {@link DEROctetString}.
     *
     * @param octetString {@link DEROctetString} to be wrapped
     */
    public DEROctetStringBC(DEROctetString octetString) {
        super(octetString);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DEROctetString}.
     */
    public DEROctetString getDEROctetString() {
        return (DEROctetString) getPrimitive();
    }
}
