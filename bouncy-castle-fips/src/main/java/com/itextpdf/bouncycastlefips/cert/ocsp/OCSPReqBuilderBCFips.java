package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionsBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;

public class OCSPReqBuilderBCFips implements IOCSPReqBuilder {
    private final OCSPReqBuilder reqBuilder;

    public OCSPReqBuilderBCFips(OCSPReqBuilder reqBuilder) {
        this.reqBuilder = reqBuilder;
    }

    public OCSPReqBuilder getReqBuilder() {
        return reqBuilder;
    }

    @Override
    public IOCSPReqBuilder setRequestExtensions(IExtensions extensions) {
        reqBuilder.setRequestExtensions(((ExtensionsBCFips) extensions).getExtensions());
        return this;
    }

    @Override
    public IOCSPReqBuilder addRequest(ICertificateID certificateID) {
        reqBuilder.addRequest(((CertificateIDBCFips) certificateID).getCertificateID());
        return this;
    }

    @Override
    public IOCSPReq build() throws OCSPExceptionBCFips {
        try {
            return new OCSPReqBCFips(reqBuilder.build());
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }
}
