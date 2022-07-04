package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;

public class ASN1PrimitiveBCFips extends ASN1EncodableBCFips implements IASN1Primitive {
    
    public ASN1PrimitiveBCFips(ASN1Primitive primitive) {
        super(primitive);
    }

    public ASN1PrimitiveBCFips(byte[] array) throws IOException {
        super(ASN1Primitive.fromByteArray(array));
    }

    public ASN1Primitive getPrimitive() {
        return (ASN1Primitive) getEncodable();
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
