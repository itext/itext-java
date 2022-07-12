package com.itextpdf.bouncycastle.asn1.util;

import com.itextpdf.commons.bouncycastle.asn1.util.IASN1Dump;

import java.util.Objects;
import org.bouncycastle.asn1.util.ASN1Dump;

public class ASN1DumpBC implements IASN1Dump {
    private static final ASN1DumpBC INSTANCE = new ASN1DumpBC(null);
    private final ASN1Dump asn1Dump;

    public ASN1DumpBC(ASN1Dump asn1Dump) {
        this.asn1Dump = asn1Dump;
    }

    public static ASN1DumpBC getInstance() {
        return INSTANCE;
    }

    public ASN1Dump getAsn1Dump() {
        return asn1Dump;
    }

    @Override
    public String dumpAsString(Object obj, boolean b) {
        return ASN1Dump.dumpAsString(obj, b);
    }

    @Override
    public String dumpAsString(Object obj) {
        return ASN1Dump.dumpAsString(obj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1DumpBC that = (ASN1DumpBC) o;
        return Objects.equals(asn1Dump, that.asn1Dump);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asn1Dump);
    }

    @Override
    public String toString() {
        return asn1Dump.toString();
    }
}
