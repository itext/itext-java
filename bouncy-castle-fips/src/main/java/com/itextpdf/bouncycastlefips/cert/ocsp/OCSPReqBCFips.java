package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPReq;

public class OCSPReqBCFips implements IOCSPReq {
    private final OCSPReq ocspReq;

    public OCSPReqBCFips(OCSPReq ocspReq) {
        this.ocspReq = ocspReq;
    }

    public OCSPReq getOcspReq() {
        return ocspReq;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return ocspReq.getEncoded();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPReqBCFips that = (OCSPReqBCFips) o;
        return Objects.equals(ocspReq, that.ocspReq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ocspReq);
    }

    @Override
    public String toString() {
        return ocspReq.toString();
    }
}
