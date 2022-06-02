package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;

public class ASN1InputStreamBCFips implements IASN1InputStream {
    private final ASN1InputStream stream;

    public ASN1InputStreamBCFips(byte[] bytes) {
        this.stream = new ASN1InputStream(bytes);
    }

    public ASN1InputStreamBCFips(InputStream stream) {
        this.stream = new ASN1InputStream(stream);
    }

    public IASN1Primitive readObject() throws IOException {
        return new ASN1PrimitiveBCFips(stream.readObject());
    }
}
