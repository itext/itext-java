/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1OutputStream;

/**
 * Wrapper class for {@link ASN1OutputStream}.
 */
public class ASN1OutputStreamBCFips implements IASN1OutputStream {
    private final ASN1OutputStream stream;

    /**
     * Creates new wrapper instance for {@link ASN1OutputStream}.
     *
     * @param stream OutputStream to create {@link ASN1OutputStream} to be wrapped
     */
    public ASN1OutputStreamBCFips(OutputStream stream) {
        this.stream = ASN1OutputStream.create(stream);
    }

    /**
     * Creates new wrapper instance for {@link ASN1OutputStream}.
     *
     * @param stream {@link ASN1OutputStream} to be wrapped
     */
    public ASN1OutputStreamBCFips(ASN1OutputStream stream) {
        this.stream = stream;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1OutputStream}.
     */
    public ASN1OutputStream getASN1OutputStream() {
        return stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObject(IASN1Primitive primitive) throws IOException {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        stream.writeObject(primitiveBCFips.getPrimitive());
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
        ASN1OutputStreamBCFips that = (ASN1OutputStreamBCFips) o;
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
