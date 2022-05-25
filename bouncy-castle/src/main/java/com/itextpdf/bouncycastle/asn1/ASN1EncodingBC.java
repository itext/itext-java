package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;
import org.bouncycastle.asn1.ASN1Encoding;

public class ASN1EncodingBC implements IASN1Encoding {

    private static final ASN1EncodingBC INSTANCE = new ASN1EncodingBC();

    private ASN1EncodingBC() {
        // empty constructor.
    }

    public static ASN1EncodingBC getInstance() {
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
