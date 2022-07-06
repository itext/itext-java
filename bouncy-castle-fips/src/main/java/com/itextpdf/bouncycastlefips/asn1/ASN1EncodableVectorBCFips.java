package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.bouncycastlefips.asn1.cms.AttributeBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.ASN1EncodableVector;

public class ASN1EncodableVectorBCFips implements IASN1EncodableVector {
    private final ASN1EncodableVector encodableVector;

    public ASN1EncodableVectorBCFips() {
        encodableVector = new ASN1EncodableVector();
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
}
