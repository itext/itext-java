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

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encoding;

/**
 * Wrapper class for {@link ASN1Encoding}.
 */
public class ASN1EncodingBC implements IASN1Encoding {
    private static final ASN1EncodingBC INSTANCE = new ASN1EncodingBC(null);

    private final ASN1Encoding asn1Encoding;

    /**
     * Creates new wrapper instance for {@link ASN1Encoding}.
     *
     * @param asn1Encoding {@link ASN1Encoding} to be wrapped
     */
    public ASN1EncodingBC(ASN1Encoding asn1Encoding) {
        this.asn1Encoding = asn1Encoding;
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link ASN1EncodingBC} instance.
     */
    public static ASN1EncodingBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Encoding}.
     */
    public ASN1Encoding getASN1Encoding() {
        return asn1Encoding;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDer() {
        return ASN1Encoding.DER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBer() {
        return ASN1Encoding.BER;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1EncodingBC that = (ASN1EncodingBC) o;
        return Objects.equals(asn1Encoding, that.asn1Encoding);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(asn1Encoding);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return asn1Encoding.toString();
    }
}
