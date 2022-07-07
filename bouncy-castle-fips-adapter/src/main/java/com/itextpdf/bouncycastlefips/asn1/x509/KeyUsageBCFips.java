package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyUsage;

import org.bouncycastle.asn1.x509.KeyUsage;

public class KeyUsageBCFips extends ASN1EncodableBCFips implements IKeyUsage {
    private static final KeyUsageBCFips INSTANCE = new KeyUsageBCFips(null);

    public KeyUsageBCFips(KeyUsage keyUsage) {
        super(keyUsage);
    }

    public static KeyUsageBCFips getInstance() {
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
