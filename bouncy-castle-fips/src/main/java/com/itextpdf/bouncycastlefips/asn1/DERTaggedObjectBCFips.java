package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;

public class DERTaggedObjectBCFips extends ASN1TaggedObjectBCFips implements IDERTaggedObject {
    public DERTaggedObjectBCFips(int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(i, encodable));
    }

    public DERTaggedObjectBCFips(boolean b, int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(b, i, encodable));
    }
}
