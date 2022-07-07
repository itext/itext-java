package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class ASN1SetBC extends ASN1PrimitiveBC implements IASN1Set {
    public ASN1SetBC(ASN1Set set) {
        super(set);
    }

    public ASN1SetBC(ASN1TaggedObject taggedObject, boolean b) {
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
        return new ASN1EncodableBC(getASN1Set().getObjectAt(index));
    }

    @Override
    public IASN1Encodable[] toArray() {
        ASN1Encodable[] encodables = getASN1Set().toArray();
        ASN1EncodableBC[] encodablesBC = new ASN1EncodableBC[encodables.length];
        for (int i = 0; i < encodables.length; ++i) {
            encodablesBC[i] = new ASN1EncodableBC(encodables[i]);
        }
        return encodablesBC;
    }
}
