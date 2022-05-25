package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import org.bouncycastle.asn1.ASN1EncodableVector;

public class ASN1EncodableVectorBC implements IASN1EncodableVector {
    private ASN1EncodableVector encodableVector;

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
}
