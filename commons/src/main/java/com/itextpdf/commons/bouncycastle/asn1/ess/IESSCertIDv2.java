package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

/**
 * This interface represents the wrapper for ESSCertIDv2 that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IESSCertIDv2 extends IASN1Encodable {
    /**
     * Calls actual {@code getHashAlgorithm} method for the wrapped ESSCertIDv2 object.
     *
     * @return {@link IAlgorithmIdentifier} hash algorithm wrapper.
     */
    IAlgorithmIdentifier getHashAlgorithm();

    /**
     * Calls actual {@code getCertHash} method for the wrapped ESSCertIDv2 object.
     *
     * @return certificate hash byte array.
     */
    byte[] getCertHash();
}
