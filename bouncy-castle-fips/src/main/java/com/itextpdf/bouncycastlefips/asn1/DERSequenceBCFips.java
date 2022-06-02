package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;

public class DERSequenceBCFips extends ASN1SequenceBCFips implements IDERSequence {
    public DERSequenceBCFips(ASN1EncodableVector vector) {
        super(new DERSequence(vector));
    }

    public DERSequenceBCFips(ASN1Encodable encodable) {
        super(new DERSequence(encodable));
    }
}
