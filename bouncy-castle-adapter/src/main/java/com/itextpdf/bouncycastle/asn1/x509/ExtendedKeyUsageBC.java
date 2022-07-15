package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtendedKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;

/**
 * Wrapper class for {@link ExtendedKeyUsage}.
 */
public class ExtendedKeyUsageBC extends ASN1EncodableBC implements IExtendedKeyUsage {
    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param extendedKeyUsage {@link ExtendedKeyUsage} to be wrapped
     */
    public ExtendedKeyUsageBC(ExtendedKeyUsage extendedKeyUsage) {
        super(extendedKeyUsage);
    }

    /**
     * Creates new wrapper instance for {@link ExtendedKeyUsage}.
     *
     * @param purposeId KeyPurposeId wrapper
     */
    public ExtendedKeyUsageBC(IKeyPurposeId purposeId) {
        super(new ExtendedKeyUsage(((KeyPurposeIdBC) purposeId).getKeyPurposeId()));
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
