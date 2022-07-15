package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERNull;

import org.bouncycastle.asn1.DERNull;

/**
 * Wrapper class for {@link DERNull}.
 */
public class DERNullBCFips extends ASN1PrimitiveBCFips implements IDERNull {
    /**
     * Wrapper for {@link DERNull} INSTANCE.
     */
    public static final DERNullBCFips INSTANCE = new DERNullBCFips();

    private DERNullBCFips() {
        super(DERNull.INSTANCE);
    }

    /**
     * Creates new wrapper instance for {@link DERNull}.
     *
     * @param derNull {@link DERNull} to be wrapped
     */
    public DERNullBCFips(DERNull derNull) {
        super(derNull);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERNull}.
     */
    public DERNull getDERNull() {
        return (DERNull) getPrimitive();
    }
}
