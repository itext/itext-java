package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

public interface IESSCertIDv2 {
    IAlgorithmIdentifier getHashAlgorithm();

    byte[] getCertHash();
}
