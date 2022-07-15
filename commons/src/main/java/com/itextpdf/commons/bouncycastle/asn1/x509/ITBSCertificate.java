package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;

/**
 * This interface represents the wrapper for TBSCertificate that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITBSCertificate extends IASN1Encodable {
    /**
     * Calls actual {@code getSubjectPublicKeyInfo} method for the wrapped TBSCertificate object.
     *
     * @return {@link ISubjectPublicKeyInfo} wrapped SubjectPublicKeyInfo.
     */
    ISubjectPublicKeyInfo getSubjectPublicKeyInfo();

    /**
     * Calls actual {@code getIssuer} method for the wrapped TBSCertificate object.
     *
     * @return {@link IX500Name} wrapped X500Name.
     */
    IX500Name getIssuer();

    /**
     * Calls actual {@code getSerialNumber} method for the wrapped TBSCertificate object.
     *
     * @return {@link IASN1Integer} wrapped ASN1Integer.
     */
    IASN1Integer getSerialNumber();
}
