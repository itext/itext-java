package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

public interface IExtension extends IASN1Encodable {
    IASN1ObjectIdentifier getCRlDistributionPoints();

    IASN1ObjectIdentifier getAuthorityInfoAccess();
}
