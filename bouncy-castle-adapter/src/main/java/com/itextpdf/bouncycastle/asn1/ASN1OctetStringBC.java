package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;

import org.bouncycastle.asn1.ASN1OctetString;

/**
 * Wrapper class for {@link ASN1OctetString}.
 */
public class ASN1OctetStringBC extends ASN1PrimitiveBC implements IASN1OctetString {
    /**
     * Creates new wrapper instance for {@link ASN1OctetString}.
     *
     * @param string {@link ASN1OctetString} to be wrapped
     */
    public ASN1OctetStringBC(ASN1OctetString string) {
        super(string);
    }

    /**
     * Creates new wrapper instance for {@link ASN1OctetString}.
     *
     * @param taggedObject ASN1TaggedObject wrapper to create {@link ASN1OctetString}
     * @param b            boolean to create {@link ASN1OctetString}
     */
    public ASN1OctetStringBC(IASN1TaggedObject taggedObject, boolean b) {
        super(ASN1OctetString.getInstance(((ASN1TaggedObjectBC) taggedObject).getASN1TaggedObject(), b));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1OctetString}.
     */
    public ASN1OctetString getASN1OctetString() {
        return (ASN1OctetString) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getOctets() {
        return getASN1OctetString().getOctets();
    }
}
