package com.itextpdf.commons.bouncycastle.asn1.util;

/**
 * This interface represents the wrapper for ASN1Dump that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Dump {
    /**
     * Calls actual {@code dumpAsString} method for the wrapped ASN1Dump object.
     *
     * @param obj the ASN1Primitive (or its wrapper) to be dumped out
     * @param b   if true, dump out the contents of octet and bit strings
     *
     * @return the resulting string.
     */
    String dumpAsString(Object obj, boolean b);

    /**
     * Calls actual {@code dumpAsString} method for the wrapped ASN1Dump object.
     *
     * @param obj the ASN1Primitive (or its wrapper) to be dumped out
     *
     * @return the resulting string.
     */
    String dumpAsString(Object obj);
}
