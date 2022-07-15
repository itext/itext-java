package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IBasicConstraints;

import org.bouncycastle.asn1.x509.BasicConstraints;

public class BasicConstraintsBCFips extends ASN1EncodableBCFips implements IBasicConstraints {
    public BasicConstraintsBCFips(BasicConstraints basicConstraints) {
        super(basicConstraints);
    }

    public BasicConstraints getBasicConstraints() {
        return (BasicConstraints) getEncodable();
    }
}
