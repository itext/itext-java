package com.itextpdf.bouncycastlefips.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;

public class DERSequenceBCFips extends DERSequence {
    public DERSequenceBCFips(ASN1EncodableVector vector) {
        super(vector);
    }

    public DERSequenceBCFips(ASN1Encodable encodable) {
        super(encodable);
    }
}
