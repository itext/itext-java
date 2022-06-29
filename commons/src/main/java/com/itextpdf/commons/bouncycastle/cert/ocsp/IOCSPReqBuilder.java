package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;

public interface IOCSPReqBuilder {
    IOCSPReqBuilder setRequestExtensions(IExtensions extensions);

    IOCSPReqBuilder addRequest(ICertificateID certificateID);

    IOCSPReq build() throws AbstractOCSPException;
}
