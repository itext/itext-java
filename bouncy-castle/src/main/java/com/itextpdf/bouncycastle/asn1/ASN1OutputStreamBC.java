package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OutputStream;

public class ASN1OutputStreamBC implements IASN1OutputStream {
    private final ASN1OutputStream stream;

    public ASN1OutputStreamBC(OutputStream stream) {
        this.stream = ASN1OutputStream.create(stream);
    }

    @Override
    public void writeObject(IASN1Primitive primitive) throws IOException {
        ASN1PrimitiveBC primitiveBC = (ASN1PrimitiveBC) primitive;
        stream.writeObject(primitiveBC.getPrimitive());
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
