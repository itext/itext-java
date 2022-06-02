package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.bouncycastle.asn1.cms.AttributeBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.ASN1EncodableVector;

public class ASN1EncodableVectorBC implements IASN1EncodableVector {
    private final ASN1EncodableVector encodableVector;

    public ASN1EncodableVectorBC() {
        encodableVector = new ASN1EncodableVector();
    }

    public ASN1EncodableVector getEncodableVector() {
        return encodableVector;
    }

    @Override
    public void add(IASN1Primitive primitive) {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        encodableVector.add(primitiveBC.getPrimitive());
    }

    @Override
    public void add(IAttribute attribute) {
        AttributeBC attributeBC = (AttributeBC) attribute;
        encodableVector.add(attributeBC.getAttribute());
    }

    @Override
    public void add(IAlgorithmIdentifier element) {
        AlgorithmIdentifierBC elementBc = (AlgorithmIdentifierBC) element;
        encodableVector.add(elementBc.getAlgorithmIdentifier());
    }
}
