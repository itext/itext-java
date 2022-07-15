package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;

import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;

/**
 * Wrapper class for {@link BasicOCSPResponse}.
 */
public class BasicOCSPResponseBC extends ASN1EncodableBC implements IBasicOCSPResponse {
    /**
     * Creates new wrapper instance for {@link BasicOCSPResponse}.
     *
     * @param basicOCSPResponse {@link BasicOCSPResponse} to be wrapped
     */
    public BasicOCSPResponseBC(BasicOCSPResponse basicOCSPResponse) {
        super(basicOCSPResponse);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link BasicOCSPResponse}.
     */
    public BasicOCSPResponse getBasicOCSPResponse() {
        return (BasicOCSPResponse) getEncodable();
    }
}
