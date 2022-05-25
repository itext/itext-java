package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;

import java.io.IOException;

public class ASN1PrimitiveBC implements ASN1EncodableBC, IASN1Primitive {
    private ASN1Primitive primitive;

    public ASN1PrimitiveBC(ASN1Primitive primitive) {
        this.primitive = primitive;
    }

    public ASN1Primitive getPrimitive() {
        return primitive;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return getPrimitive().getEncoded();
    }

    @Override
    public byte[] getEncoded(String encoding) throws IOException {
        return getPrimitive().getEncoded(encoding);
    }
}
