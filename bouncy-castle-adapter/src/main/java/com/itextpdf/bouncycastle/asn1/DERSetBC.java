package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSet;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;

public class DERSetBC extends ASN1SetBC implements IDERSet {
    public DERSetBC(DERSet derSet) {
        super(derSet);
    }

    public DERSetBC(ASN1EncodableVector vector) {
        super(new DERSet(vector));
    }

    public DERSetBC(ASN1Encodable encodable) {
        super(new DERSet(encodable));
    }

    public DERSet getDERSet() {
        return (DERSet) getEncodable();
    }
}
