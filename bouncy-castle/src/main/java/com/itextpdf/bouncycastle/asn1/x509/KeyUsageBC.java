package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;

import org.bouncycastle.asn1.x509.KeyUsage;

public class KeyUsageBC extends ASN1EncodableBC implements IKeyUsage {
    private static final KeyUsageBC INSTANCE = new KeyUsageBC(null);

    public KeyUsageBC(KeyUsage keyUsage) {
        super(keyUsage);
    }

    public static KeyUsageBC getInstance() {
        return INSTANCE;
    }

    public KeyUsage getKeyUsage() {
        return (KeyUsage) getEncodable();
    }

    @Override
    public int getDigitalSignature() {
        return KeyUsage.digitalSignature;
    }

    @Override
    public int getNonRepudiation() {
        return KeyUsage.nonRepudiation;
    }
}
