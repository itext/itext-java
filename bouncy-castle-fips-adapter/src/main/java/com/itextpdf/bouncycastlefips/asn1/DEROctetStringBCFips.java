package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;

import org.bouncycastle.asn1.DEROctetString;

public class DEROctetStringBCFips extends ASN1OctetStringBCFips implements IDEROctetString {
    public DEROctetStringBCFips(byte[] bytes) {
        super(new DEROctetString(bytes));
    }

    public DEROctetStringBCFips(DEROctetString octetString) {
        super(octetString);
    }

    public DEROctetString getDEROctetString() {
        return (DEROctetString) getPrimitive();
    }
}
