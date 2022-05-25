package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;

import java.io.IOException;

public class DEROctetStringBC extends ASN1OctetStringBC implements IDEROctetString {
    public DEROctetStringBC(byte[] bytes) {
        super(new DEROctetString(bytes));
    }

    public DEROctetStringBC(ASN1Encodable asn1Encodable) throws IOException {
        super(new DEROctetString(asn1Encodable));
    }

    public DEROctetString getDEROctetString() {
        return (DEROctetString) getPrimitive();
    }
}
