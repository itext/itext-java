package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;

import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;

public class BasicOCSPRespBCFips implements IBasicOCSPResp {
    private final BasicOCSPResp basicOCSPRespBC;

    public BasicOCSPRespBCFips(BasicOCSPResp basicOCSPRespBC) {
        this.basicOCSPRespBC = basicOCSPRespBC;
    }

    public BasicOCSPResp getBasicOCSPRespBC() {
        return basicOCSPRespBC;
    }

    @Override
    public ISingleResp[] getResponses() {
        SingleResp[] resps = basicOCSPRespBC.getResponses();
        ISingleResp[] respsBC = new ISingleResp[resps.length];
        for (int i = 0; i < respsBC.length; i++) {
            respsBC[i] = new SingleRespBCFips(resps[i]);
        }
        return respsBC;
    }
}
