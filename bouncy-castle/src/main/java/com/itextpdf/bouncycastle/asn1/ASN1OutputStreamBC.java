package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import org.bouncycastle.asn1.ASN1OutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class ASN1OutputStreamBC implements IASN1OutputStream {
    private ASN1OutputStream stream;

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
