package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;

/**
 * Wrapper class for {@link ASN1Set}.
 */
public class ASN1SetBCFips extends ASN1PrimitiveBCFips implements IASN1Set {
    /**
     * Creates new wrapper instance for {@link ASN1Set}.
     *
     * @param set {@link ASN1Set} to be wrapped
     */
    public ASN1SetBCFips(ASN1Set set) {
        super(set);
    }

    /**
     * Creates new wrapper instance for {@link ASN1Set}.
     *
     * @param taggedObject {@link ASN1TaggedObject} to create {@link ASN1Set} to be wrapped
     * @param b            boolean to create {@link ASN1Set} to be wrapped
     */
    public ASN1SetBCFips(ASN1TaggedObject taggedObject, boolean b) {
        super(ASN1Set.getInstance(taggedObject, b));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Set}.
     */
    public ASN1Set getASN1Set() {
        return (ASN1Set) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration getObjects() {
        return getASN1Set().getObjects();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return getASN1Set().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable getObjectAt(int index) {
        return new ASN1EncodableBCFips(getASN1Set().getObjectAt(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable[] toArray() {
        ASN1Encodable[] encodables = getASN1Set().toArray();
        ASN1EncodableBCFips[] encodablesBCFips = new ASN1EncodableBCFips[encodables.length];
        for (int i = 0; i < encodables.length; ++i) {
            encodablesBCFips[i] = new ASN1EncodableBCFips(encodables[i]);
        }
        return encodablesBCFips;
    }
}
