package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder;

import org.bouncycastle.cert.ocsp.OCSPRespBuilder;

public class OCSPRespBuilderBCFips implements IOCSPRespBuilder {
    private static final OCSPRespBuilderBCFips INSTANCE = new OCSPRespBuilderBCFips(null);

    private static final int SUCCESSFUL = OCSPRespBuilder.SUCCESSFUL;

    private final OCSPRespBuilder ocspRespBuilder;

    public OCSPRespBuilderBCFips(OCSPRespBuilder ocspRespBuilder) {
        this.ocspRespBuilder = ocspRespBuilder;
    }

    public static OCSPRespBuilderBCFips getInstance() {
        return INSTANCE;
    }

    public OCSPRespBuilder getOcspRespBuilder() {
        return ocspRespBuilder;
    }

    @Override
    public int getSuccessful() {
        return SUCCESSFUL;
    }
}
