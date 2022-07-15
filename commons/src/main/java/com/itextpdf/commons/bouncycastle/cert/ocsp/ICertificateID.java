package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.math.BigInteger;

/**
 * This interface represents the wrapper for CertificateID that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ICertificateID {
    /**
     * Calls actual {@code getHashAlgOID} method for the wrapped CertificateID object.
     *
     * @return {@link IASN1ObjectIdentifier} hash algorithm OID wrapper.
     */
    IASN1ObjectIdentifier getHashAlgOID();

    /**
     * Gets {@code getHashSha1} constant for the wrapped CertificateID.
     *
     * @return CertificateID.HASH_SHA1 wrapper.
     */
    IAlgorithmIdentifier getHashSha1();

    /**
     * Calls actual {@code matchesIssuer} method for the wrapped CertificateID object.
     *
     * @param certificateHolder X509CertificateHolder wrapper
     * @param provider          DigestCalculatorProvider wrapper
     *
     * @return boolean value.
     *
     * @throws AbstractOCSPException OCSPException wrapper.
     */
    boolean matchesIssuer(IX509CertificateHolder certificateHolder, IDigestCalculatorProvider provider)
            throws AbstractOCSPException;

    /**
     * Calls actual {@code getSerialNumber} method for the wrapped CertificateID object.
     *
     * @return serial number value.
     */
    BigInteger getSerialNumber();
}
