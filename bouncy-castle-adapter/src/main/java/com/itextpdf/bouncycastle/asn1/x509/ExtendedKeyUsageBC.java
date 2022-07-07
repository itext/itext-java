package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtendedKeyUsage;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;

public class ExtendedKeyUsageBC extends ASN1EncodableBC implements IExtendedKeyUsage {
    public ExtendedKeyUsageBC(ExtendedKeyUsage extendedKeyUsage) {
        super(extendedKeyUsage);
    }

    public ExtendedKeyUsageBC(IKeyPurposeId purposeId) {
        super(new ExtendedKeyUsage(((KeyPurposeIdBC) purposeId).getKeyPurposeId()));
    }

    public ExtendedKeyUsage getExtendedKeyUsage() {
        return (ExtendedKeyUsage) getEncodable();
    }
}
