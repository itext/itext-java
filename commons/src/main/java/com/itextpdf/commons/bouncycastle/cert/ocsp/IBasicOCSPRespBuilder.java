package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.util.Date;

public interface IBasicOCSPRespBuilder {
    IBasicOCSPRespBuilder setResponseExtensions(IExtensions extensions);

    IBasicOCSPRespBuilder addResponse(ICertificateID certID, ICertificateStatus certificateStatus, Date time, Date time1, IExtensions extensions);

    IBasicOCSPResp build(IContentSigner signer, IX509CertificateHolder[] chain, Date time) throws AbstractOCSPException;
}
