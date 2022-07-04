package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPResponseBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;

import java.io.IOException;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;

public class OCSPRespBCFips implements IOCSPResp {
    private final OCSPResp ocspResp;

    public OCSPRespBCFips(OCSPResp ocspResp) {
        this.ocspResp = ocspResp;
    }

    public OCSPRespBCFips(IOCSPResponse ocspResponse) {
        this(new OCSPResp(((OCSPResponseBCFips) ocspResponse).getOcspResponse()));
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
    public Object getResponseObject() throws OCSPExceptionBCFips {
        try {
            return ocspResp.getResponseObject();
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }
}
