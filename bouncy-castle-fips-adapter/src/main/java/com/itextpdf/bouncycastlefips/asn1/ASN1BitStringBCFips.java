package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1BitString;

import org.bouncycastle.asn1.ASN1BitString;

/**
 * Wrapper class for {@link ASN1BitString}.
 */
public class ASN1BitStringBCFips extends ASN1PrimitiveBCFips implements IASN1BitString {
    /**
     * Creates new wrapper instance for {@link ASN1BitString}.
     *
     * @param asn1BitString {@link ASN1BitString} to be wrapped
     */
    public ASN1BitStringBCFips(ASN1BitString asn1BitString) {
        super(asn1BitString);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1BitString}.
     */
    public ASN1BitString getASN1BitString() {
        return (ASN1BitString) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return getASN1BitString().getString();
    }
}
