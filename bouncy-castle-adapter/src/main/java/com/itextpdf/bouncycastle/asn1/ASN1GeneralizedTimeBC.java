package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1GeneralizedTime;

import org.bouncycastle.asn1.ASN1GeneralizedTime;

/**
 * Wrapper class for {@link ASN1GeneralizedTime}.
 */
public class ASN1GeneralizedTimeBC extends ASN1PrimitiveBC implements IASN1GeneralizedTime {
    /**
     * Creates new wrapper instance for {@link ASN1GeneralizedTime}.
     *
     * @param asn1GeneralizedTime {@link ASN1GeneralizedTime} to be wrapped
     */
    public ASN1GeneralizedTimeBC(ASN1GeneralizedTime asn1GeneralizedTime) {
        super(asn1GeneralizedTime);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1GeneralizedTime}.
     */
    public ASN1GeneralizedTime getASN1GeneralizedTime() {
        return (ASN1GeneralizedTime) getEncodable();
    }
}
