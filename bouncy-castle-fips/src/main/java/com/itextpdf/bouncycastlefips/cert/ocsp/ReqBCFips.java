package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.Req;

public class ReqBCFips implements IReq {
    public final Req req;

    public ReqBCFips(Req req) {
        this.req = req;
    }

    public Req getReq() {
        return req;
    }

    @Override
    public ICertificateID getCertID() {
        return new CertificateIDBCFips(req.getCertID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReqBCFips reqBCFips = (ReqBCFips) o;
        return Objects.equals(req, reqBCFips.req);
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
