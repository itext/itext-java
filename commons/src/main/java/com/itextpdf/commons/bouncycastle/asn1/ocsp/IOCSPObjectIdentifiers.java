package com.itextpdf.commons.bouncycastle.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for OCSPObjectIdentifiers that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPObjectIdentifiers {
    /**
     * Gets {@code id_pkix_ocsp_basic} constant for the wrapped OCSPObjectIdentifiers.
     *
     * @return OCSPObjectIdentifiers.id_pkix_ocsp_basic wrapper.
     */
    IASN1ObjectIdentifier getIdPkixOcspBasic();

    /**
     * Gets {@code id_pkix_ocsp_nonce} constant for the wrapped OCSPObjectIdentifiers.
     *
     * @return OCSPObjectIdentifiers.id_pkix_ocsp_nonce wrapper.
     */
    IASN1ObjectIdentifier getIdPkixOcspNonce();

    /**
     * Gets {@code id_pkix_ocsp_nocheck} constant for the wrapped OCSPObjectIdentifiers.
     *
     * @return OCSPObjectIdentifiers.id_pkix_ocsp_nocheck wrapper.
     */
    IASN1ObjectIdentifier getIdPkixOcspNoCheck();
}
