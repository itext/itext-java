package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;

import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;

public class BasicOCSPResponseBCFips extends ASN1EncodableBCFips implements IBasicOCSPResponse {
    public BasicOCSPResponseBCFips(BasicOCSPResponse basicOCSPResponse) {
        super(basicOCSPResponse);
    }

    public BasicOCSPResponse getBasicOCSPResponse() {
        return (BasicOCSPResponse) getEncodable();
    }
}
