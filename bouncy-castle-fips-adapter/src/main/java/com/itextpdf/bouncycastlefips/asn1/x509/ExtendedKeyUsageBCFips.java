package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtendedKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;

/**
 * Wrapper class for {@link ExtendedKeyUsage}.
 */
public class ExtendedKeyUsageBCFips extends ASN1EncodableBCFips implements IExtendedKeyUsage {
    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param extendedKeyUsage {@link ExtendedKeyUsage} to be wrapped
     */
    public ExtendedKeyUsageBCFips(ExtendedKeyUsage extendedKeyUsage) {
        super(extendedKeyUsage);
    }

    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param purposeId KeyPurposeId wrapper
     */
    public ExtendedKeyUsageBCFips(IKeyPurposeId purposeId) {
        super(new ExtendedKeyUsage(((KeyPurposeIdBCFips) purposeId).getKeyPurposeId()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ExtendedKeyUsage}.
     */
    public ExtendedKeyUsage getExtendedKeyUsage() {
        return (ExtendedKeyUsage) getEncodable();
    }
}
