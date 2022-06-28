package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;

public class ASN1SequenceBCFips extends ASN1PrimitiveBCFips implements IASN1Sequence {
    public ASN1SequenceBCFips(ASN1Sequence sequence) {
        super(sequence);
    }

    public ASN1SequenceBCFips(Object obj) {
        super(ASN1Sequence.getInstance(obj));
    }

    public ASN1Sequence getSequence() {
        return (ASN1Sequence) getPrimitive();
    }

    public IASN1Encodable getObjectAt(int i) {
        return new ASN1EncodableBCFips(getSequence().getObjectAt(i));
    }

    @Override
    public Enumeration getObjects() {
        return getSequence().getObjects();
    }

    public int size() {
        return getSequence().size();
    }
}
