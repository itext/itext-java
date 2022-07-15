package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;

import org.bouncycastle.asn1.DERIA5String;

/**
 * Wrapper class for {@link DERIA5String}.
 */
public class DERIA5StringBC extends ASN1PrimitiveBC implements IDERIA5String {
    /**
     * Creates new wrapper instance for {@link DERIA5String}.
     *
     * @param deria5String {@link DERIA5String} to be wrapped
     */
    public DERIA5StringBC(DERIA5String deria5String) {
        super(deria5String);
    }

    /**
     * Creates new wrapper instance for {@link DERIA5String}.
     *
     * @param str string to create {@link DERIA5String} to be wrapped
     */
    public DERIA5StringBC(String str) {
        this(new DERIA5String(str));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERIA5String}.
     */
    public DERIA5String getDerIA5String() {
        return (DERIA5String) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return getDerIA5String().getString();
    }
}
