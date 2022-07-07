package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encoding;

public class ASN1EncodingBC implements IASN1Encoding {
    private static final ASN1EncodingBC INSTANCE = new ASN1EncodingBC(null);

    private final ASN1Encoding asn1Encoding;

    public ASN1EncodingBC(ASN1Encoding asn1Encoding) {
        this.asn1Encoding = asn1Encoding;
    }

    public static ASN1EncodingBC getInstance() {
        return INSTANCE;
    }

    public ASN1Encoding getASN1Encoding() {
        return asn1Encoding;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1EncodingBC that = (ASN1EncodingBC) o;
        return Objects.equals(asn1Encoding, that.asn1Encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asn1Encoding);
    }

    @Override
    public String toString() {
        return asn1Encoding.toString();
    }
}
