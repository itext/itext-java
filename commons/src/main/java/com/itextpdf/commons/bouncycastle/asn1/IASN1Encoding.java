package com.itextpdf.commons.bouncycastle.asn1;

/**
 * This interface represents the wrapper for ASN1Encoding that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1Encoding {
    /**
     * Gets {@code DER} constant for the wrapped ASN1Encoding.
     *
     * @return ASN1Encoding.DER value.
     */
    String getDer();

    /**
     * Gets {@code BER} constant for the wrapped ASN1Encoding.
     *
     * @return ASN1Encoding.BER value.
     */
    String getBer();
}
