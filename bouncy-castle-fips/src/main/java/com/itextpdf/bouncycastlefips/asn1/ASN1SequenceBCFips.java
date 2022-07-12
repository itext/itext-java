package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;

public class ASN1SequenceBCFips extends ASN1PrimitiveBCFips implements IASN1Sequence {
    public ASN1SequenceBCFips(ASN1Sequence sequence) {
        super(sequence);
    }

    public ASN1SequenceBCFips(Object obj) {
        super(ASN1Sequence.getInstance(obj));
    }

    public ASN1Sequence getASN1Sequence() {
        return (ASN1Sequence) getPrimitive();
    }

    public IASN1Encodable getObjectAt(int i) {
        return new ASN1EncodableBCFips(getASN1Sequence().getObjectAt(i));
    }

    @Override
    public Enumeration getObjects() {
        return getASN1Sequence().getObjects();
    }

    @Override
    public int size() {
        return getASN1Sequence().size();
    }

    @Override
    public IASN1Encodable[] toArray() {
        ASN1Encodable[] encodables = getASN1Sequence().toArray();
        ASN1EncodableBCFips[] encodablesBCFips = new ASN1EncodableBCFips[encodables.length];
        for (int i = 0; i < encodables.length; ++i) {
            encodablesBCFips[i] = new ASN1EncodableBCFips(encodables[i]);
        }
        return encodablesBCFips;
    }
}
