package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERNull;

import org.bouncycastle.asn1.DERNull;

/**
 * Wrapper class for {@link DERNull}.
 */
public class DERNullBC extends ASN1PrimitiveBC implements IDERNull {
    /**
     * Wrapper for {@link DERNull} INSTANCE.
     */
    public static final DERNullBC INSTANCE = new DERNullBC();

    private DERNullBC() {
        super(DERNull.INSTANCE);
    }

    /**
     * Creates new wrapper instance for {@link DERNull}.
     *
     * @param derNull {@link DERNull} to be wrapped
     */
    public DERNullBC(DERNull derNull) {
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
