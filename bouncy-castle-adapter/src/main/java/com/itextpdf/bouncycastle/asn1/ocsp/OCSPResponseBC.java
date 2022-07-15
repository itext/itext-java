package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.OCSPResponse;

/**
 * Wrapper class for {@link OCSPResponse}.
 */
public class OCSPResponseBC extends ASN1EncodableBC implements IOCSPResponse {
    /**
     * Creates new wrapper instance for {@link OCSPResponse}.
     *
     * @param ocspResponse {@link OCSPResponse} to be wrapped
     */
    public OCSPResponseBC(OCSPResponse ocspResponse) {
        super(ocspResponse);
    }

    /**
     * Creates new wrapper instance for {@link OCSPResponse}.
     *
     * @param respStatus    OCSPResponseStatus wrapper
     * @param responseBytes ResponseBytes wrapper
     */
    public OCSPResponseBC(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        super(new OCSPResponse(
                ((OCSPResponseStatusBC) respStatus).getOcspResponseStatus(),
                ((ResponseBytesBC) responseBytes).getResponseBytes()));
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
