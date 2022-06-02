package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERNull;

import org.bouncycastle.asn1.DERNull;

public class DERNullBCFips extends ASN1PrimitiveBCFips implements IDERNull {
    public static DERNullBCFips INSTANCE = new DERNullBCFips();

    private DERNullBCFips() {
        super(DERNull.INSTANCE);
    }
}
