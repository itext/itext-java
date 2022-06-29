package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OutputStream;

public class ASN1OutputStreamBCFips implements IASN1OutputStream {
    private final ASN1OutputStream stream;

    public ASN1OutputStreamBCFips(OutputStream stream) {
        this.stream = new ASN1OutputStream(stream);
    }

    public ASN1OutputStreamBCFips(ASN1OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void writeObject(IASN1Primitive primitive) throws IOException {
        ASN1PrimitiveBCFips primitiveBC = (ASN1PrimitiveBCFips) primitive;
        stream.writeObject(primitiveBC.getPrimitive());
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
