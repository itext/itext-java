package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encodable;

public class ASN1EncodableBCFips implements IASN1Encodable {
    private final ASN1Encodable encodable;

    public ASN1EncodableBCFips(ASN1Encodable encodable) {
        this.encodable = encodable;
    }

    public ASN1Encodable getEncodable() {
        return encodable;
    }

    @Override
    public IASN1Primitive toASN1Primitive() {
        return new ASN1PrimitiveBCFips(encodable.toASN1Primitive());
    }

    @Override
    public boolean isNull() {
        return encodable == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1EncodableBCFips that = (ASN1EncodableBCFips) o;
        return Objects.equals(encodable, that.encodable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encodable);
    }

    @Override
    public String toString() {
        return encodable.toString();
    }
}
