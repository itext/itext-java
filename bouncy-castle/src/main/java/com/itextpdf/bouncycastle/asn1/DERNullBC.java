package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERNull;

import org.bouncycastle.asn1.DERNull;

public class DERNullBC extends ASN1PrimitiveBC implements IDERNull {
    public static DERNullBC INSTANCE = new DERNullBC();

    private DERNullBC() {
        super(DERNull.INSTANCE);
    }

    public DERNullBC(DERNull derNull) {
        super(derNull);
    }

    public DERNull getDERNull() {
        return (DERNull) getPrimitive();
    }
}
