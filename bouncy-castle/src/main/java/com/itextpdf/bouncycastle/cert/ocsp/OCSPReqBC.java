package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;

import java.io.IOException;
import org.bouncycastle.cert.ocsp.OCSPReq;

public class OCSPReqBC implements IOCSPReq {
    private final OCSPReq ocspReq;

    public OCSPReqBC(OCSPReq ocspReq) {
        this.ocspReq = ocspReq;
    }

    public OCSPReq getOcspReq() {
        return ocspReq;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return ocspReq.getEncoded();
    }
}
