package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder;

import org.bouncycastle.cert.ocsp.OCSPRespBuilder;

public class OCSPRespBuilderBC implements IOCSPRespBuilder {
    private static final OCSPRespBuilderBC INSTANCE = new OCSPRespBuilderBC(null);

    private static final int SUCCESSFUL = OCSPRespBuilder.SUCCESSFUL;

    private final OCSPRespBuilder ocspRespBuilder;

    public OCSPRespBuilderBC(OCSPRespBuilder ocspRespBuilder) {
        this.ocspRespBuilder = ocspRespBuilder;
    }

    public static OCSPRespBuilderBC getInstance() {
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
