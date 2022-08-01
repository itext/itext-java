package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;

import java.text.ParseException;
import java.util.Date;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;

/**
 * Wrapper class for {@link BasicOCSPResponse}.
 */
public class BasicOCSPResponseBCFips extends ASN1EncodableBCFips implements IBasicOCSPResponse {
    /**
     * Creates new wrapper instance for {@link BasicOCSPResponse}.
     *
     * @param basicOCSPResponse {@link BasicOCSPResponse} to be wrapped
     */
    public BasicOCSPResponseBCFips(BasicOCSPResponse basicOCSPResponse) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getProducedAtDate() throws ParseException {
        return getBasicOCSPResponse().getTbsResponseData().getProducedAt().getDate();
    }
}
