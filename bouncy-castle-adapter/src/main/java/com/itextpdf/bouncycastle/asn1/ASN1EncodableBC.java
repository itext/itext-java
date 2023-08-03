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
package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encodable;

/**
 * Wrapper class for {@link ASN1Encodable}.
 */
public class ASN1EncodableBC implements IASN1Encodable {
    private final ASN1Encodable encodable;

    /**
     * Creates new wrapper instance for {@link ASN1Encodable}.
     *
     * @param encodable {@link ASN1Encodable} to be wrapped
     */
    public ASN1EncodableBC(ASN1Encodable encodable) {
        this.encodable = encodable;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Encodable}.
     */
    public ASN1Encodable getEncodable() {
        return encodable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive toASN1Primitive() {
        return new ASN1PrimitiveBC(encodable.toASN1Primitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNull() {
        return encodable == null;
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
        ASN1EncodableBC that = (ASN1EncodableBC) o;
        return Objects.equals(encodable, that.encodable);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(encodable);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return encodable.toString();
    }
}
