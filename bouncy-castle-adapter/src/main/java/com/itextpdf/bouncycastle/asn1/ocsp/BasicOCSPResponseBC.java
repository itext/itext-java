package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;

import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;

public class BasicOCSPResponseBC extends ASN1EncodableBC implements IBasicOCSPResponse {
    public BasicOCSPResponseBC(BasicOCSPResponse basicOCSPResponse) {
        super(basicOCSPResponse);
    }

    public BasicOCSPResponse getBasicOCSPResponse() {
        return (BasicOCSPResponse) getEncodable();
    }
}
