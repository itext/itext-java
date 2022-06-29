package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReq;

public class OCSPReqBCFips implements IOCSPReq {
    private final OCSPReq ocspReq;

    public OCSPReqBCFips(OCSPReq ocspReq) {
        this.ocspReq = ocspReq;
    }

    public OCSPReq getOcspReq() {
        return ocspReq;
    }
}
