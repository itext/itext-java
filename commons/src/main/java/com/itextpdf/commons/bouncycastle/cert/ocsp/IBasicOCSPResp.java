package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.io.IOException;
import java.util.Date;

public interface IBasicOCSPResp {
    ISingleResp[] getResponses();

    boolean isSignatureValid(IContentVerifierProvider provider) throws AbstractOCSPException;

    IX509CertificateHolder[] getCerts();

    byte[] getEncoded() throws IOException;

    Date getProducedAt();
}
