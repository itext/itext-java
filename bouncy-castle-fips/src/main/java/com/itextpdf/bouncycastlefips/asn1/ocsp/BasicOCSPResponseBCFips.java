package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;

import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;

public class BasicOCSPResponseBCFips implements IBasicOCSPResponse {
    private final BasicOCSPResponse basicOCSPResponse;

    public BasicOCSPResponseBCFips(BasicOCSPResponse basicOCSPResponse) {
        this.basicOCSPResponse = basicOCSPResponse;
    }

    public BasicOCSPResponse getBasicOCSPResponse() {
        return basicOCSPResponse;
    }
}
