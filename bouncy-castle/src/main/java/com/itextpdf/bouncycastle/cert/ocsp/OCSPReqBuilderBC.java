package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.x509.ExtensionsBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;

public class OCSPReqBuilderBC implements IOCSPReqBuilder {
    private final OCSPReqBuilder reqBuilder;
    
    public OCSPReqBuilderBC(OCSPReqBuilder reqBuilder) {
        this.reqBuilder = reqBuilder;
    }

    public OCSPReqBuilder getReqBuilder() {
        return reqBuilder;
    }

    @Override
    public IOCSPReqBuilder setRequestExtensions(IExtensions extensions) {
        reqBuilder.setRequestExtensions(((ExtensionsBC) extensions).getExtensions());
        return this;
    }
    
    @Override
    public IOCSPReqBuilder addRequest(ICertificateID certificateID) {
        reqBuilder.addRequest(((CertificateIDBC) certificateID).getCertificateID());
        return this;
    }
    
    @Override
    public IOCSPReq build() throws OCSPExceptionBC {
        try {
            return new OCSPReqBC(reqBuilder.build());
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }
}
