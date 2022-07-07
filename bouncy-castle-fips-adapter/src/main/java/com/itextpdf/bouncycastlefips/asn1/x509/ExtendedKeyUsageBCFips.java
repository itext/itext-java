package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtendedKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;

public class ExtendedKeyUsageBCFips extends ASN1EncodableBCFips implements IExtendedKeyUsage {
    public ExtendedKeyUsageBCFips(ExtendedKeyUsage extendedKeyUsage) {
        super(extendedKeyUsage);
    }

    public ExtendedKeyUsageBCFips(IKeyPurposeId purposeId) {
        super(new ExtendedKeyUsage(((KeyPurposeIdBCFips) purposeId).getKeyPurposeId()));
    }

    public ExtendedKeyUsage getExtendedKeyUsage() {
        return (ExtendedKeyUsage) getEncodable();
    }
}
