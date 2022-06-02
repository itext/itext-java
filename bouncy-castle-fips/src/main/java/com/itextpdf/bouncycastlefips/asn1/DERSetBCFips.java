package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSet;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;

public class DERSetBCFips extends ASN1SetBCFips implements IDERSet {
    public DERSetBCFips(ASN1EncodableVector vector) {
        super(new DERSet(vector));
    }

    public DERSetBCFips(ASN1Encodable encodable) {
        super(new DERSet(encodable));
    }
}
