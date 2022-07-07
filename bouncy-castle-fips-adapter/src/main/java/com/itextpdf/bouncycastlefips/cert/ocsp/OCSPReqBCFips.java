package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.Req;

public class OCSPReqBCFips implements IOCSPReq {
    private final OCSPReq ocspReq;

    public OCSPReqBCFips(OCSPReq ocspReq) {
        this.ocspReq = ocspReq;
    }

    public OCSPReq getOcspReq() {
        return ocspReq;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return ocspReq.getEncoded();
    }

    @Override
    public IReq[] getRequestList() {
        Req[] reqs = ocspReq.getRequestList();
        IReq[] reqsBCFips = new IReq[reqs.length];
        for (int i = 0; i < reqs.length; ++i) {
            reqsBCFips[i] = new ReqBCFips(reqs[i]);
        }
        return reqsBCFips;
    }

    @Override
    public IExtension getExtension(IASN1ObjectIdentifier objectIdentifier) {
        return new ExtensionBCFips(ocspReq.getExtension(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPReqBCFips that = (OCSPReqBCFips) o;
        return Objects.equals(ocspReq, that.ocspReq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ocspReq);
    }

    @Override
    public String toString() {
        return ocspReq.toString();
    }
}
