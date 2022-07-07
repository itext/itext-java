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

    public ASN1Set getASN1Set() {
        return (ASN1Set) getPrimitive();
    }

    @Override
    public Enumeration getObjects() {
        return getASN1Set().getObjects();
    }

    @Override
    public int size() {
        return getASN1Set().size();
    }

    @Override
    public IASN1Encodable getObjectAt(int index) {
        return new ASN1EncodableBCFips(getASN1Set().getObjectAt(index));
    }
}
