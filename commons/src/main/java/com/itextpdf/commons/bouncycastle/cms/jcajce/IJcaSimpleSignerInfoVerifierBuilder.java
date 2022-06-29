package com.itextpdf.commons.bouncycastle.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;

import java.security.cert.X509Certificate;

public interface IJcaSimpleSignerInfoVerifierBuilder {
    IJcaSimpleSignerInfoVerifierBuilder setProvider(String provider);

    ISignerInformationVerifier build(X509Certificate certificate) throws AbstractOperatorCreationException;
}
