package com.itextpdf.commons.bouncycastle.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

public interface IOCSPObjectIdentifiers {
    IASN1ObjectIdentifier getIdPkixOcspBasic();

    IASN1ObjectIdentifier getIdPkixOcspNonce();

    IASN1ObjectIdentifier getIdPkixOcspNoCheck();
}
