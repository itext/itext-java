package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ocsp.OCSPResponseBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;

public class OCSPRespBC implements IOCSPResp {
    private static final OCSPRespBC INSTANCE = new OCSPRespBC((OCSPResp) null);

    private final OCSPResp ocspResp;

    public OCSPRespBC(OCSPResp ocspResp) {
        this.ocspResp = ocspResp;
    }

    public OCSPRespBC(IOCSPResponse ocspResponse) {
        this(new OCSPResp(((OCSPResponseBC) ocspResponse).getOcspResponse()));
    }

    public static OCSPRespBC getInstance() {
        return INSTANCE;
    }

    public OCSPResp getOcspResp() {
        return ocspResp;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return ocspResp.getEncoded();
    }

    @Override
    public int getStatus() {
        return ocspResp.getStatus();
    }

    @Override
    public Object getResponseObject() throws OCSPExceptionBC {
        try {
            return ocspResp.getResponseObject();
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    @Override
    public int getSuccessful() {
        return OCSPResp.SUCCESSFUL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPRespBC that = (OCSPRespBC) o;
        return Objects.equals(ocspResp, that.ocspResp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ocspResp);
    }

    @Override
    public String toString() {
        return ocspResp.toString();
    }
}
