package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface IDistributionPointName extends IASN1Encodable {
    int getType();

    IASN1Encodable getName();

    int getFullName();
}
