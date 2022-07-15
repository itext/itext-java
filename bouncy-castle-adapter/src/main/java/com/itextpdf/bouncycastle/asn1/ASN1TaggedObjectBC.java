package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;

import org.bouncycastle.asn1.ASN1TaggedObject;

/**
 * Wrapper class for {@link ASN1TaggedObject}.
 */
public class ASN1TaggedObjectBC extends ASN1PrimitiveBC implements IASN1TaggedObject {
    /**
     * Creates new wrapper instance for {@link ASN1TaggedObject}.
     *
     * @param taggedObject {@link ASN1TaggedObject} to be wrapped
     */
    public ASN1TaggedObjectBC(ASN1TaggedObject taggedObject) {
        super(taggedObject);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1TaggedObject}.
     */
    public ASN1TaggedObject getASN1TaggedObject() {
        return (ASN1TaggedObject) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive getObject() {
        return new ASN1PrimitiveBC(getASN1TaggedObject().getObject());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTagNo() {
        return getASN1TaggedObject().getTagNo();
    }
}
