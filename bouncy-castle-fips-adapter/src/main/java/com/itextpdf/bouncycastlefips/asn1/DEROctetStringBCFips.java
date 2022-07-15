package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;

import org.bouncycastle.asn1.DEROctetString;

/**
 * Wrapper class for {@link DEROctetString}.
 */
public class DEROctetStringBCFips extends ASN1OctetStringBCFips implements IDEROctetString {
    /**
     * Creates new wrapper instance for {@link DEROctetString}.
     *
     * @param bytes byte array to create {@link DEROctetString} to be wrapped
     */
    public DEROctetStringBCFips(byte[] bytes) {
        super(new DEROctetString(bytes));
    }

    /**
     * Creates new wrapper instance for {@link DEROctetString}.
     *
     * @param octetString {@link DEROctetString} to be wrapped
     */
    public DEROctetStringBCFips(DEROctetString octetString) {
        super(octetString);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DEROctetString}.
     */
    public DEROctetString getDEROctetString() {
        return (DEROctetString) getPrimitive();
    }
}
