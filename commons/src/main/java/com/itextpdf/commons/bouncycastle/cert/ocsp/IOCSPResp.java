package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;

import java.io.IOException;

public interface IOCSPResp {
    byte[] getEncoded() throws IOException;

    int getStatus();

    Object getResponseObject() throws AbstractOCSPException;
}
