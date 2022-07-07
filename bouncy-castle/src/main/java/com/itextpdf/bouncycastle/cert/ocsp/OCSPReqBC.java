package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;

import java.io.IOException;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPReqBC ocspReqBC = (OCSPReqBC) o;
        return Objects.equals(ocspReq, ocspReqBC.ocspReq);
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
