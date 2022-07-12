package com.itextpdf.commons.bouncycastle.cert.ocsp;

public interface IOCSPRespBuilder {
    int getSuccessful();

    IOCSPResp build(int i, IBasicOCSPResp basicOCSPResp) throws AbstractOCSPException;
}
