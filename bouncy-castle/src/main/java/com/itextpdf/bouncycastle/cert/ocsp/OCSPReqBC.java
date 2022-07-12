package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.ExtensionBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.Req;

public class OCSPReqBC implements IOCSPReq {
    private final OCSPReq ocspReq;

    public OCSPReqBC(OCSPReq ocspReq) {
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
        IReq[] reqsBC = new IReq[reqs.length];
        for (int i = 0; i < reqs.length; ++i) {
            reqsBC[i] = new ReqBC(reqs[i]);
        }
        return reqsBC;
    }

    @Override
    public IExtension getExtension(IASN1ObjectIdentifier objectIdentifier) {
        return new ExtensionBC(ocspReq.getExtension(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPReqBC ocspReqBC = (OCSPReqBC) o;
        return Objects.equals(ocspReq, ocspReqBC.ocspReq);
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
