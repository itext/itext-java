package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;
import org.bouncycastle.asn1.ASN1Integer;

import java.math.BigInteger;

public class ASN1IntegerBC extends ASN1PrimitiveBC implements IASN1Integer {

    public ASN1IntegerBC(ASN1Integer i) {
        super(i);
    }

    public ASN1IntegerBC(int i) {
        super(new ASN1Integer(i));
    }

    public ASN1IntegerBC(BigInteger i) {
        super(new ASN1Integer(i));
    }

    public ASN1Integer getInteger() {
        return (ASN1Integer) getPrimitive();
    }

    @Override
    public BigInteger getValue() {
        return getInteger().getValue();
    }
}
