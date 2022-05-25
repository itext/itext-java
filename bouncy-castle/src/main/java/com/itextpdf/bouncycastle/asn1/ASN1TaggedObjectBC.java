package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class ASN1TaggedObjectBC extends ASN1PrimitiveBC implements IASN1TaggedObject {
    public ASN1TaggedObjectBC(ASN1TaggedObject taggedObject) {
        super(taggedObject);
    }

    public ASN1TaggedObject getTaggedObject() {
        return (ASN1TaggedObject) getPrimitive();
    }

    @Override
    public IASN1Primitive getObject() {
        return new ASN1PrimitiveBC(getTaggedObject().getObject());
    }

    @Override
    public int getTagNo() {
        return getTaggedObject().getTagNo();
    }
}
