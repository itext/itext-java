package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;

import org.bouncycastle.asn1.ASN1TaggedObject;

/**
 * Wrapper class for {@link ASN1TaggedObject}.
 */
public class ASN1TaggedObjectBCFips extends ASN1PrimitiveBCFips implements IASN1TaggedObject {
    /**
     * Creates new wrapper instance for {@link ASN1TaggedObject}.
     *
     * @param taggedObject {@link ASN1TaggedObject} to be wrapped
     */
    public ASN1TaggedObjectBCFips(ASN1TaggedObject taggedObject) {
        super(taggedObject);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1TaggedObject}.
     */
    public ASN1TaggedObject getTaggedObject() {
        return (ASN1TaggedObject) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive getObject() {
        return new ASN1PrimitiveBCFips(getTaggedObject().getObject());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTagNo() {
        return getTaggedObject().getTagNo();
    }
}
