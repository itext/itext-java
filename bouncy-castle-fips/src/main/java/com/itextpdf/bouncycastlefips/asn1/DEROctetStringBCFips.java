package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;

public class DEROctetStringBCFips extends ASN1OctetStringBCFips implements IDEROctetString {
    public DEROctetStringBCFips(byte[] bytes) {
        super(new DEROctetString(bytes));
    }

    public DEROctetStringBCFips(ASN1Encodable asn1Encodable) throws IOException {
        super(new DEROctetString(asn1Encodable));
    }

    public DEROctetString getDEROctetString() {
        return (DEROctetString) getPrimitive();
    }
}
