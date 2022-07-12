package com.itextpdf.commons.bouncycastle.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public interface IJcaSignerInfoGeneratorBuilder {
    ISignerInfoGenerator build(IContentSigner signer, X509Certificate cert)
            throws AbstractOperatorCreationException, CertificateEncodingException;
}
