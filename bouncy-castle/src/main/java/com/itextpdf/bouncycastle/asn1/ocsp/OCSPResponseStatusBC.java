package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;

import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;

public class OCSPResponseStatusBC extends ASN1EncodableBC implements IOCSPResponseStatus {
    private static final OCSPResponseStatusBC INSTANCE = new OCSPResponseStatusBC(null);

    private static final int SUCCESSFUL = OCSPResponseStatus.SUCCESSFUL;

    public OCSPResponseStatusBC(OCSPResponseStatus ocspResponseStatus) {
        super(ocspResponseStatus);
    }

    public static IOCSPResponseStatus getInstance() {
        return INSTANCE;
    }

    public OCSPResponseStatus getOcspResponseStatus() {
        return (OCSPResponseStatus) getEncodable();
    }

    @Override
    public int getSuccessful() {
        return SUCCESSFUL;
    }
}
