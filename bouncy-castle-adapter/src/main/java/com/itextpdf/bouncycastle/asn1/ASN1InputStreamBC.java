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

import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1InputStream;

/**
 * Wrapper class for {@link ASN1InputStream}.
 */
public class ASN1InputStreamBC implements IASN1InputStream {
    private final ASN1InputStream stream;

    /**
     * Creates new wrapper instance for {@link ASN1InputStream}.
     *
     * @param asn1InputStream {@link ASN1InputStream} to be wrapped
     */
    public ASN1InputStreamBC(ASN1InputStream asn1InputStream) {
        this.stream = asn1InputStream;
    }

    /**
     * Creates new wrapper instance for {@link ASN1InputStream}.
     *
     * @param bytes byte array to create {@link ASN1InputStream}
     */
    public ASN1InputStreamBC(byte[] bytes) {
        this.stream = new ASN1InputStream(bytes);
    }

    /**
     * Creates new wrapper instance for {@link ASN1InputStream}.
     *
     * @param stream InputStream to create {@link ASN1InputStream}
     */
    public ASN1InputStreamBC(InputStream stream) {
        this.stream = new ASN1InputStream(stream);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1InputStream}.
     */
    public ASN1InputStream getASN1InputStream() {
        return stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive readObject() throws IOException {
        return new ASN1PrimitiveBC(stream.readObject());
    }

    /**
     * Delegates {@code close} method call to the wrapped stream.
     */
    @Override
    public void close() throws IOException {
        stream.close();
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
        ASN1InputStreamBC that = (ASN1InputStreamBC) o;
        return Objects.equals(stream, that.stream);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(stream);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return stream.toString();
    }
}
