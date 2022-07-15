package com.itextpdf.commons.bouncycastle.cert.ocsp;

/**
 * This interface represents the wrapper for CertificateStatus that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ICertificateStatus {
    /**
     * Gets {@code GOOD} constant for the wrapped CertificateStatus.
     *
     * @return CertificateStatus.GOOD wrapper.
     */
    ICertificateStatus getGood();
}
