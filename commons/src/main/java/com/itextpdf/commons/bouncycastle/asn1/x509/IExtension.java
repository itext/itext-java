package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for Extension that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IExtension extends IASN1Encodable {
    /**
     * Gets {@code cRLDistributionPoints} constant for the wrapped Extension.
     *
     * @return Extension.cRLDistributionPoints wrapper.
     */
    IASN1ObjectIdentifier getCRlDistributionPoints();

    /**
     * Gets {@code authorityInfoAccess} constant for the wrapped Extension.
     *
     * @return Extension.authorityInfoAccess wrapper.
     */
    IASN1ObjectIdentifier getAuthorityInfoAccess();

    /**
     * Gets {@code basicConstraints} constant for the wrapped Extension.
     *
     * @return Extension.basicConstraints wrapper.
     */
    IASN1ObjectIdentifier getBasicConstraints();

    /**
     * Gets {@code keyUsage} constant for the wrapped Extension.
     *
     * @return Extension.keyUsage wrapper.
     */
    IASN1ObjectIdentifier getKeyUsage();

    /**
     * Gets {@code extendedKeyUsage} constant for the wrapped Extension.
     *
     * @return Extension.extendedKeyUsage wrapper.
     */
    IASN1ObjectIdentifier getExtendedKeyUsage();

    /**
     * Gets {@code authorityKeyIdentifier} constant for the wrapped Extension.
     *
     * @return Extension.authorityKeyIdentifier wrapper.
     */
    IASN1ObjectIdentifier getAuthorityKeyIdentifier();

    /**
     * Gets {@code subjectKeyIdentifier} constant for the wrapped Extension.
     *
     * @return Extension.subjectKeyIdentifier wrapper.
     */
    IASN1ObjectIdentifier getSubjectKeyIdentifier();
}
