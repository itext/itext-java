package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IBasicConstraints;

import org.bouncycastle.asn1.x509.BasicConstraints;

public class BasicConstraintsBC extends ASN1EncodableBC implements IBasicConstraints {
    public BasicConstraintsBC(BasicConstraints basicConstraints) {
        super(basicConstraints);
    }

    public BasicConstraints getBasicConstraints() {
        return (BasicConstraints) getEncodable();
    }
}
