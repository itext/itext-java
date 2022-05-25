package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DERSequenceBC extends ASN1SequenceBC implements IDERSequence {
    public DERSequenceBC(ASN1EncodableVector vector) {
        super(new DERSequence(vector));
    }

    public DERSequenceBC(ASN1Encodable encodable) {
        super(new DERSequence(encodable));
    }
}
