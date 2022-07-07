package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1String;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1String;

public class ASN1StringBCFips implements IASN1String {
    private final ASN1String asn1String;

    public ASN1StringBCFips(ASN1String asn1String) {
        this.asn1String = asn1String;
    }

    public ASN1String getAsn1String() {
        return asn1String;
    }

    @Override
    public String getString() {
        return asn1String.getString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1StringBCFips that = (ASN1StringBCFips) o;
        return Objects.equals(asn1String, that.asn1String);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asn1String);
    }

    @Override
    public String toString() {
        return asn1String.toString();
    }
}
