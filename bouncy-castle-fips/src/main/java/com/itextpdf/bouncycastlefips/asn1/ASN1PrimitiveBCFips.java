package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;

public class ASN1PrimitiveBCFips implements ASN1EncodableBCFips, IASN1Primitive {
    private final ASN1Primitive primitive;

    public ASN1PrimitiveBCFips(ASN1Primitive primitive) {
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
