package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.OCSPResponse;

/**
 * Wrapper class for {@link OCSPResponse}.
 */
public class OCSPResponseBCFips extends ASN1EncodableBCFips implements IOCSPResponse {
    /**
     * Creates new wrapper instance for {@link OCSPResponse}.
     *
     * @param ocspResponse {@link OCSPResponse} to be wrapped
     */
    public OCSPResponseBCFips(OCSPResponse ocspResponse) {
        super(ocspResponse);
    }

    /**
     * Creates new wrapper instance for {@link OCSPResponse}.
     *
     * @param respStatus    OCSPResponseStatus wrapper
     * @param responseBytes ResponseBytes wrapper
     */
    public OCSPResponseBCFips(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        super(new OCSPResponse(
                ((OCSPResponseStatusBCFips) respStatus).getOcspResponseStatus(),
                ((ResponseBytesBCFips) responseBytes).getResponseBytes()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPResponse}.
     */
    public OCSPResponse getOcspResponse() {
        return (OCSPResponse) getEncodable();
    }
}
