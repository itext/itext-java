package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;
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

    @Override
    public IOCSPResp build(int i, IBasicOCSPResp basicOCSPResp) throws OCSPExceptionBC {
        try {
            return new OCSPRespBC(ocspRespBuilder.build(i, ((BasicOCSPRespBC) basicOCSPResp).getBasicOCSPResp()));
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPRespBuilderBC that = (OCSPRespBuilderBC) o;
        return Objects.equals(ocspRespBuilder, that.ocspRespBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ocspRespBuilder);
    }

    @Override
    public String toString() {
        return ocspRespBuilder.toString();
    }
}
