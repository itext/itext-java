package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;

import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;

public class BasicOCSPResponseBC implements IBasicOCSPResponse {
    private final BasicOCSPResponse basicOCSPResponse;

    public BasicOCSPResponseBC(BasicOCSPResponse basicOCSPResponse) {
        this.basicOCSPResponse = basicOCSPResponse;
    }

    public BasicOCSPResponse getBasicOCSPResponse() {
        return basicOCSPResponse;
    }
}
