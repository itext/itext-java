package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/**
 * Wrapper class for {@link ASN1Integer}.
 */
public class ASN1IntegerBC extends ASN1PrimitiveBC implements IASN1Integer {
    /**
     * Creates new wrapper instance for {@link ASN1Integer}.
     *
     * @param i {@link ASN1Integer} to be wrapped
     */
    public ASN1IntegerBC(ASN1Integer i) {
        super(i);
    }

    /**
     * Creates new wrapper instance for {@link ASN1Integer}.
     *
     * @param i int value to create {@link ASN1Integer} to be wrapped
     */
    public ASN1IntegerBC(int i) {
        super(new ASN1Integer(i));
    }

    /**
     * Creates new wrapper instance for {@link ASN1Integer}.
     *
     * @param i BigInteger value to create {@link ASN1Integer} to be wrapped
     */
    public ASN1IntegerBC(BigInteger i) {
        super(new ASN1Integer(i));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Integer}.
     */
    public ASN1Integer getASN1Integer() {
        return (ASN1Integer) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getValue() {
        return getASN1Integer().getValue();
    }
}
