package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;

import org.bouncycastle.asn1.x509.KeyUsage;

/**
 * Wrapper class for {@link KeyUsage}.
 */
public class KeyUsageBCFips extends ASN1EncodableBCFips implements IKeyUsage {
    private static final KeyUsageBCFips INSTANCE = new KeyUsageBCFips(null);

    /**
     * Creates new wrapper instance for {@link KeyUsage}.
     *
     * @param keyUsage {@link KeyUsage} to be wrapped
     */
    public KeyUsageBCFips(KeyUsage keyUsage) {
        super(keyUsage);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link KeyUsageBCFips} instance.
     */
    public static KeyUsageBCFips getInstance() {
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
