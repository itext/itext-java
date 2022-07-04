package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.math.BigInteger;

public interface ICertificateID {
    IASN1ObjectIdentifier getHashAlgOID();

    IAlgorithmIdentifier getHashSha1();

    boolean matchesIssuer(IX509CertificateHolder certificateHolder,
                          IDigestCalculatorProvider provider) throws AbstractOCSPException;

    BigInteger getSerialNumber();
}
