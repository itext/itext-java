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

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;

/**
 * Wrapper class for {@link ASN1Primitive}.
 */
public class ASN1PrimitiveBC extends ASN1EncodableBC implements IASN1Primitive {
    /**
     * Creates new wrapper instance for {@link ASN1Primitive}.
     *
     * @param primitive {@link ASN1Primitive} to be wrapped
     */
    public ASN1PrimitiveBC(ASN1Primitive primitive) {
        super(primitive);
    }

    /**
     * Creates new wrapper instance for {@link ASN1Primitive}.
     *
     * @param array byte array to create {@link ASN1Primitive} to be wrapped
     * @throws IOException if {@link ASN1Primitive} cannot be created from byte array.
     */
    public ASN1PrimitiveBC(byte[] array) throws IOException {
        super(ASN1Primitive.fromByteArray(array));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Primitive}.
     */
    public ASN1Primitive getPrimitive() {
        return (ASN1Primitive) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return getPrimitive().getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded(String encoding) throws IOException {
        return getPrimitive().getEncoded(encoding);
    }
}
