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
package com.itextpdf.bouncycastlefips.asn1.util;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.util.IASN1Dump;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.util.ASN1Dump;

/**
 * Wrapper class for {@link ASN1Dump}.
 */
public class ASN1DumpBCFips implements IASN1Dump {
    private static final ASN1DumpBCFips INSTANCE = new ASN1DumpBCFips(null);

    private final ASN1Dump asn1Dump;

    /**
     * Creates new wrapper instance for {@link ASN1Dump}.
     *
     * @param asn1Dump {@link ASN1Dump} to be wrapped
     */
    public ASN1DumpBCFips(ASN1Dump asn1Dump) {
        this.asn1Dump = asn1Dump;
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link ASN1DumpBCFips} instance.
     */
    public static ASN1DumpBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Dump}.
     */
    public ASN1Dump getAsn1Dump() {
        return asn1Dump;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String dumpAsString(Object obj, boolean b) {
        if (obj instanceof ASN1EncodableBCFips) {
            return ASN1Dump.dumpAsString(((ASN1EncodableBCFips) obj).getEncodable(), b);
        }
        return ASN1Dump.dumpAsString((ASN1Encodable) obj, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String dumpAsString(Object obj) {
        if (obj instanceof ASN1EncodableBCFips) {
            return ASN1Dump.dumpAsString(((ASN1EncodableBCFips) obj).getEncodable());
        }
        return ASN1Dump.dumpAsString((ASN1Encodable) obj);
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
        ASN1DumpBCFips that = (ASN1DumpBCFips) o;
        return Objects.equals(asn1Dump, that.asn1Dump);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(asn1Dump);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return asn1Dump.toString();
    }
}
