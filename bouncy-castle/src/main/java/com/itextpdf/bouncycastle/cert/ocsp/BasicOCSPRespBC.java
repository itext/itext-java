package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.operator.ContentVerifierProviderBC;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.SingleResp;

public class BasicOCSPRespBC implements IBasicOCSPResp {
    private final BasicOCSPResp basicOCSPResp;

    public BasicOCSPRespBC(BasicOCSPResp basicOCSPRespBC) {
        this.basicOCSPResp = basicOCSPRespBC;
    }

    public BasicOCSPResp getBasicOCSPResp() {
        return basicOCSPResp;
    }

    @Override
    public ISingleResp[] getResponses() {
        SingleResp[] resps = basicOCSPResp.getResponses();
        ISingleResp[] respsBC = new ISingleResp[resps.length];
        for (int i = 0; i < respsBC.length; i++) {
            respsBC[i] = new SingleRespBC(resps[i]);
        }
        return respsBC;
    }

    @Override
    public boolean isSignatureValid(IContentVerifierProvider provider) throws OCSPExceptionBC {
        try {
            return basicOCSPResp.isSignatureValid(
                    ((ContentVerifierProviderBC) provider).getContentVerifierProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    @Override
    public IX509CertificateHolder[] getCerts() {
        X509CertificateHolder[] certs = basicOCSPResp.getCerts();
        IX509CertificateHolder[] certsBC = new IX509CertificateHolder[certs.length];
        for (int i = 0; i < certs.length; i++) {
            certsBC[i] = new X509CertificateHolderBC(certs[i]);
        }
        return certsBC;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return basicOCSPResp.getEncoded();
    }

    @Override
    public Date getProducedAt() {
        return basicOCSPResp.getProducedAt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicOCSPRespBC that = (BasicOCSPRespBC) o;
        return Objects.equals(basicOCSPResp, that.basicOCSPResp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicOCSPResp);
    }

    @Override
    public String toString() {
        return basicOCSPResp.toString();
    }
}
