package com.itextpdf.commons.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface IJcaX509CertificateConverter {
    X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException;
}
