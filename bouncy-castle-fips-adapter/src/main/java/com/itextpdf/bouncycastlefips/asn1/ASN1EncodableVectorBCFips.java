package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.bouncycastlefips.asn1.cms.AttributeBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1EncodableVector;

/**
 * Wrapper class for {@link ASN1EncodableVector}.
 */
public class ASN1EncodableVectorBCFips implements IASN1EncodableVector {
    private final ASN1EncodableVector encodableVector;

    /**
     * Creates new wrapper instance for new {@link ASN1EncodableVector} object.
     */
    public ASN1EncodableVectorBCFips() {
        encodableVector = new ASN1EncodableVector();
    }

    /**
     * Creates new wrapper instance for {@link ASN1EncodableVector}.
     *
     * @param encodableVector {@link ASN1EncodableVector} to be wrapped
     */
    public ASN1EncodableVectorBCFips(ASN1EncodableVector encodableVector) {
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
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        encodableVector.add(primitiveBCFips.getPrimitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(IAttribute attribute) {
        AttributeBCFips attributeBCFips = (AttributeBCFips) attribute;
        encodableVector.add(attributeBCFips.getAttribute());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(IAlgorithmIdentifier element) {
        AlgorithmIdentifierBCFips elementBCFips = (AlgorithmIdentifierBCFips) element;
        encodableVector.add(elementBCFips.getAlgorithmIdentifier());
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
        ASN1EncodableVectorBCFips that = (ASN1EncodableVectorBCFips) o;
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
