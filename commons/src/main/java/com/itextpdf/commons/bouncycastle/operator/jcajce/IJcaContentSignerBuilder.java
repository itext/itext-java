package com.itextpdf.commons.bouncycastle.operator.jcajce;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.security.PrivateKey;

public interface IJcaContentSignerBuilder {
    IContentSigner build(PrivateKey pk) throws AbstractOperatorCreationException;

    IJcaContentSignerBuilder setProvider(String providerName);
}
