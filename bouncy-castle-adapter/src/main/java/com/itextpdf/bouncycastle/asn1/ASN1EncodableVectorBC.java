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
