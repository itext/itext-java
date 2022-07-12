package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.Req;

public class ReqBC implements IReq {
    public final Req req;

    public ReqBC(Req req) {
        this.req = req;
    }

    public Req getReq() {
        return req;
    }

    @Override
    public ICertificateID getCertID() {
        return new CertificateIDBC(req.getCertID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReqBC reqBC = (ReqBC) o;
        return Objects.equals(req, reqBC.req);
    }

    @Override
    public int hashCode() {
        return Objects.hash(req);
    }

    @Override
    public String toString() {
        return req.toString();
    }
}
