package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;

import org.bouncycastle.asn1.ASN1TaggedObject;

public class ASN1TaggedObjectBCFips extends ASN1PrimitiveBCFips implements IASN1TaggedObject {
    public ASN1TaggedObjectBCFips(ASN1TaggedObject taggedObject) {
        super(taggedObject);
    }

    public ASN1TaggedObject getTaggedObject() {
        return (ASN1TaggedObject) getPrimitive();
    }

    @Override
    public IASN1Primitive getObject() {
        return new ASN1PrimitiveBCFips(getTaggedObject().getObject());
    }

    @Override
    public int getTagNo() {
        return getTaggedObject().getTagNo();
    }
}
