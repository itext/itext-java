package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class ASN1SetBCFips extends ASN1PrimitiveBCFips implements IASN1Set {
    public ASN1SetBCFips(ASN1Set set) {
        super(set);
    }

    public ASN1SetBCFips(ASN1TaggedObject taggedObject, boolean b) {
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
    public IASN1Encodable getObjectAt(int index) {
        return new ASN1EncodableBCFips(getSet().getObjectAt(index));
    }
}
