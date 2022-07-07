package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;

import org.bouncycastle.asn1.ASN1Enumerated;

public class ASN1EnumeratedBC extends ASN1PrimitiveBC implements IASN1Enumerated {
    public ASN1EnumeratedBC(ASN1Enumerated asn1Enumerated) {
        super(asn1Enumerated);
    }

    public ASN1EnumeratedBC(int i) {
        super(new ASN1Enumerated(i));
    }

    public ASN1Enumerated getASN1Enumerated() {
        return (ASN1Enumerated) getEncodable();
    }
}
