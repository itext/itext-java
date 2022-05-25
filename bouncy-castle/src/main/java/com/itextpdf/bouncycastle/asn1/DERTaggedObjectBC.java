package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;

public class DERTaggedObjectBC extends ASN1TaggedObjectBC implements IDERTaggedObject {
    public DERTaggedObjectBC(int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(i, encodable));
    }

    public DERTaggedObjectBC(boolean b, int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(b, i, encodable));
    }
}
