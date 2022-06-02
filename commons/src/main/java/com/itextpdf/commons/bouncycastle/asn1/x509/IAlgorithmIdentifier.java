package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

public interface IAlgorithmIdentifier {
    IASN1ObjectIdentifier getAlgorithm();
}
