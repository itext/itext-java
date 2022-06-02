package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;

import org.bouncycastle.cert.ocsp.SingleResp;

public class SingleRespBC implements ISingleResp {
    private final SingleResp singleResp;

    public SingleRespBC(SingleResp singleResp) {
        this.singleResp = singleResp;
    }

    public SingleResp getSingleResp() {
        return singleResp;
    }

    @Override
    public ICertificateID getCertID() {
        return new CertificateIDBC(singleResp.getCertID());
    }
}
