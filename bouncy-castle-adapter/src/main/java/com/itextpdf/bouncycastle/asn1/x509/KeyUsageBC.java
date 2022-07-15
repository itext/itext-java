package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;

import org.bouncycastle.asn1.x509.KeyUsage;

/**
 * Wrapper class for {@link KeyUsage}.
 */
public class KeyUsageBC extends ASN1EncodableBC implements IKeyUsage {
    private static final KeyUsageBC INSTANCE = new KeyUsageBC(null);

    /**
     * Creates new wrapper instance for {@link KeyUsage}.
     *
     * @param keyUsage {@link KeyUsage} to be wrapped
     */
    public KeyUsageBC(KeyUsage keyUsage) {
        super(keyUsage);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link KeyUsageBC} instance.
     */
    public static KeyUsageBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link KeyUsage}.
     */
    public KeyUsage getKeyUsage() {
        return (KeyUsage) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDigitalSignature() {
        return KeyUsage.digitalSignature;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNonRepudiation() {
        return KeyUsage.nonRepudiation;
    }
}
