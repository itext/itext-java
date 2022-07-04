package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;

import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;

public class OCSPResponseStatusBCFips extends ASN1EncodableBCFips implements IOCSPResponseStatus {
    private static final OCSPResponseStatusBCFips INSTANCE = new OCSPResponseStatusBCFips(null);

    private static final int SUCCESSFUL = OCSPResponseStatus.SUCCESSFUL;

    public OCSPResponseStatusBCFips(OCSPResponseStatus ocspResponseStatus) {
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
