package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableWrapper;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;

import java.util.Enumeration;

public class ASN1SetBC extends ASN1PrimitiveBC implements IASN1Set {
    public ASN1SetBC(ASN1Set set) {
        super(set);
    }

    public ASN1SetBC(ASN1TaggedObject taggedObject, boolean b) {
        super(ASN1Set.getInstance(taggedObject, b));
    }

    public ASN1Set getSet() {
        return (ASN1Set) getPrimitive();
    }

    @Override
    public Enumeration getObjects() {
        return getSet().getObjects();
    }

    @Override
    public int size() {
        return getSet().size();
    }

    @Override
    public IASN1EncodableWrapper getObjectAt(int index) {
        return new ASN1EncodableWrapperBC(getSet().getObjectAt(index));
    }
}
