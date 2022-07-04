package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ocsp.OCSPResponseBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;

import java.io.IOException;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;

public class OCSPRespBC implements IOCSPResp {
    private final OCSPResp ocspResp;

    public OCSPRespBC(OCSPResp ocspResp) {
        this.ocspResp = ocspResp;
    }

    public OCSPRespBC(IOCSPResponse ocspResponse) {
        this(new OCSPResp(((OCSPResponseBC) ocspResponse).getOcspResponse()));
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
}
