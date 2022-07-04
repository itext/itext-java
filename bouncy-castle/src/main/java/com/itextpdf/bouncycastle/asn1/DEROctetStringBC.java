package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import org.bouncycastle.asn1.DEROctetString;

public class DEROctetStringBC extends ASN1OctetStringBC implements IDEROctetString {
    public DEROctetStringBC(byte[] bytes) {
        super(new DEROctetString(bytes));
    }

    public DEROctetStringBC(DEROctetString octetString) {
        super(octetString);
    }

    public DEROctetString getDEROctetString() {
        return (DEROctetString) getPrimitive();
    }
}
