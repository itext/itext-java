package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;

import org.bouncycastle.asn1.ASN1Encoding;

public class ASN1EncodingBCFips implements IASN1Encoding {

    private static final ASN1EncodingBCFips INSTANCE = new ASN1EncodingBCFips();

    private ASN1EncodingBCFips() {
        // empty constructor.
    }

    public static ASN1EncodingBCFips getInstance() {
        return INSTANCE;
    }

    @Override
    public String getDer() {
        return ASN1Encoding.DER;
    }

    @Override
    public String getDl() {
        return ASN1Encoding.DL;
    }

    @Override
    public String getBer() {
        return ASN1Encoding.BER;
    }
}
