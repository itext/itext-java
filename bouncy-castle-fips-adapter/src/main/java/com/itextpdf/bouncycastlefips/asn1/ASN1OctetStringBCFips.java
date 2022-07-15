package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;

import org.bouncycastle.asn1.ASN1OctetString;

/**
 * Wrapper class for {@link ASN1OctetString}.
 */
public class ASN1OctetStringBCFips extends ASN1PrimitiveBCFips implements IASN1OctetString {
    /**
     * Creates new wrapper instance for {@link ASN1OctetString}.
     *
     * @param string {@link ASN1OctetString} to be wrapped
     */
    public ASN1OctetStringBCFips(ASN1OctetString string) {
        super(string);
    }

    /**
     * Creates new wrapper instance for {@link ASN1OctetString}.
     *
     * @param taggedObject ASN1TaggedObject wrapper to create {@link ASN1OctetString}
     * @param b            boolean to create {@link ASN1OctetString}
     */
    public ASN1OctetStringBCFips(IASN1TaggedObject taggedObject, boolean b) {
        super(ASN1OctetString.getInstance(((ASN1TaggedObjectBCFips) taggedObject).getTaggedObject(), b));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1OctetString}.
     */
    public ASN1OctetString getOctetString() {
        return (ASN1OctetString) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getOctets() {
        return getOctetString().getOctets();
    }
}
