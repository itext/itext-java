package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.OCSPResponse;

public class OCSPResponseBC extends ASN1EncodableBC implements IOCSPResponse {
    public OCSPResponseBC(OCSPResponse ocspResponse) {
        super(ocspResponse);
    }

    public OCSPResponseBC(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        super(new OCSPResponse(
                ((OCSPResponseStatusBC) respStatus).getOcspResponseStatus(),
                ((ResponseBytesBC) responseBytes).getResponseBytes()));
    }

    public OCSPResponse getOcspResponse() {
        return (OCSPResponse) getEncodable();
    }
}
