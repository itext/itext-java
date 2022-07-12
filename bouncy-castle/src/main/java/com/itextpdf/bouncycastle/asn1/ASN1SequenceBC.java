package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;

public class ASN1SequenceBC extends ASN1PrimitiveBC implements IASN1Sequence {
    public ASN1SequenceBC(ASN1Sequence sequence) {
        super(sequence);
    }

    public ASN1SequenceBC(Object obj) {
        super(ASN1Sequence.getInstance(obj));
    }

    public ASN1Sequence getASN1Sequence() {
        return (ASN1Sequence) getPrimitive();
    }

    public IASN1Encodable getObjectAt(int i) {
        return new ASN1EncodableBC(getASN1Sequence().getObjectAt(i));
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
        ASN1EncodableBC[] encodablesBC = new ASN1EncodableBC[encodables.length];
        for (int i = 0; i < encodables.length; ++i) {
            encodablesBC[i] = new ASN1EncodableBC(encodables[i]);
        }
        return encodablesBC;
    }
}
