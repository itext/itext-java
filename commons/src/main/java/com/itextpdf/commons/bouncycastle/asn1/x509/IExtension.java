package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

public interface IExtension extends IASN1Encodable {
    IASN1ObjectIdentifier getCRlDistributionPoints();

    IASN1ObjectIdentifier getAuthorityInfoAccess();

    IASN1ObjectIdentifier getBasicConstraints();

    IASN1ObjectIdentifier getKeyUsage();

    IASN1ObjectIdentifier getExtendedKeyUsage();

    IASN1ObjectIdentifier getAuthorityKeyIdentifier();

    IASN1ObjectIdentifier getSubjectKeyIdentifier();
}
