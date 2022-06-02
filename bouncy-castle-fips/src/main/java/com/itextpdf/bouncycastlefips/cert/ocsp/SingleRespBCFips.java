package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;

import org.bouncycastle.cert.ocsp.SingleResp;

public class SingleRespBCFips implements ISingleResp {
    private final SingleResp singleResp;

    public SingleRespBCFips(SingleResp singleResp) {
        this.singleResp = singleResp;
    }

    public SingleResp getSingleResp() {
        return singleResp;
    }

    @Override
    public ICertificateID getCertID() {
        return new CertificateIDBCFips(singleResp.getCertID());
    }
}
