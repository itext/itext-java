package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.OCSPResponse;

public class OCSPResponseBCFips extends ASN1EncodableBCFips implements IOCSPResponse {
    public OCSPResponseBCFips(OCSPResponse ocspResponse) {
        super(ocspResponse);
    }

    public OCSPResponseBCFips(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        super(new OCSPResponse(
                ((OCSPResponseStatusBCFips) respStatus).getOcspResponseStatus(),
                ((ResponseBytesBCFips) responseBytes).getResponseBytes()));
    }

    public OCSPResponse getOcspResponse() {
        return (OCSPResponse) getEncodable();
    }
}
