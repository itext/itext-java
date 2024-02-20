package com.itextpdf.signatures.validation.extensions;

/**
 * Enum representing possible "Key Usage" extension values.
 */
public enum KeyUsage {
    /**
     * "Digital Signature" key usage value
     */
    DIGITAL_SIGNATURE,
    /**
     * "Non Repudiation" key usage value
     */
    NON_REPUDIATION,
    /**
     * "Key Encipherment" key usage value
     */
    KEY_ENCIPHERMENT,
    /**
     * "Data Encipherment" key usage value
     */
    DATA_ENCIPHERMENT,
    /**
     * "Key Agreement" key usage value
     */
    KEY_AGREEMENT,
    /**
     * "Key Cert Sign" key usage value
     */
    KEY_CERT_SIGN,
    /**
     * "CRL Sign" key usage value
     */
    CRL_SIGN,
    /**
     * "Encipher Only" key usage value
     */
    ENCIPHER_ONLY,
    /**
     * "Decipher Only" key usage value
     */
    DECIPHER_ONLY
}
