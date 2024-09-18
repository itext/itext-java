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
package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.bouncycastle.asn1.cms.AttributeBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1EncodableVector;

/**
 * Wrapper class for {@link ASN1EncodableVector}.
 */
public class ASN1EncodableVectorBC implements IASN1EncodableVector {
    private final ASN1EncodableVector encodableVector;

    /**
     * Creates new wrapper instance for new {@link ASN1EncodableVector} object.
     */
    public ASN1EncodableVectorBC() {
        encodableVector = new ASN1EncodableVector();
    }

    /**
     * Creates new wrapper instance for {@link ASN1EncodableVector}.
     *
     * @param encodableVector {@link ASN1EncodableVector} to be wrapped
     */
    public ASN1EncodableVectorBC(ASN1EncodableVector encodableVector) {
        this.encodableVector = encodableVector;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1EncodableVector}.
     */
    public ASN1EncodableVector getEncodableVector() {
        return encodableVector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        encodableVector.add(primitiveBC.getPrimitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(IAttribute attribute) {
        AttributeBC attributeBC = (AttributeBC) attribute;
        encodableVector.add(attributeBC.getAttribute());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(IAlgorithmIdentifier element) {
        AlgorithmIdentifierBC elementBc = (AlgorithmIdentifierBC) element;
        encodableVector.add(elementBc.getAlgorithmIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addOptional(IASN1Primitive primitive) {
        if (primitive != null) {
            add(primitive);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addOptional(IAttribute attribute) {
        if (attribute != null) {
            add(attribute);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addOptional(IAlgorithmIdentifier element) {
        if (element != null) {
            add(element);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return encodableVector.size();
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
        ASN1EncodableVectorBC that = (ASN1EncodableVectorBC) o;
        return Objects.equals(encodableVector, that.encodableVector);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(encodableVector);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return encodableVector.toString();
    }
}
