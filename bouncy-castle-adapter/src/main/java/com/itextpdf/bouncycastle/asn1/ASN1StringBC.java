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

import com.itextpdf.commons.bouncycastle.asn1.IASN1String;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1String;

/**
 * Wrapper class for {@link ASN1String}.
 */
public class ASN1StringBC implements IASN1String {
    private final ASN1String asn1String;

    /**
     * Creates new wrapper instance for {@link ASN1String}.
     *
     * @param asn1String {@link ASN1String} to be wrapped
     */
    public ASN1StringBC(ASN1String asn1String) {
        this.asn1String = asn1String;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1String}.
     */
    public ASN1String getASN1String() {
        return asn1String;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return asn1String.getString();
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
        ASN1StringBC that = (ASN1StringBC) o;
        return Objects.equals(asn1String, that.asn1String);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(asn1String);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return asn1String.toString();
    }
}
