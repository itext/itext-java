package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

public interface ICertificateID {
    IASN1ObjectIdentifier getHashAlgOID();
}
