package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.bouncycastlefips.asn1.cms.AttributeBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1EncodableVector;

public class ASN1EncodableVectorBCFips implements IASN1EncodableVector {
    private final ASN1EncodableVector encodableVector;

    public ASN1EncodableVectorBCFips() {
        encodableVector = new ASN1EncodableVector();
    }

    public ASN1EncodableVectorBCFips(ASN1EncodableVector encodableVector) {
        this.encodableVector = encodableVector;
    }

    public ASN1EncodableVector getEncodableVector() {
        return encodableVector;
    }

    @Override
    public void add(IASN1Primitive primitive) {
        ASN1PrimitiveBCFips primitiveBCFips = (ASN1PrimitiveBCFips) primitive;
        encodableVector.add(primitiveBCFips.getPrimitive());
    }

    @Override
    public void add(IAttribute attribute) {
        AttributeBCFips attributeBCFips = (AttributeBCFips) attribute;
        encodableVector.add(attributeBCFips.getAttribute());
    }

    @Override
    public void add(IAlgorithmIdentifier element) {
        AlgorithmIdentifierBCFips elementBCFips = (AlgorithmIdentifierBCFips) element;
        encodableVector.add(elementBCFips.getAlgorithmIdentifier());
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(encodableVector);
    }

    @Override
    public String toString() {
        return encodableVector.toString();
    }
}
