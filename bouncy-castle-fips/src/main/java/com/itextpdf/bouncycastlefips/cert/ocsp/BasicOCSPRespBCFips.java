package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.operator.ContentVerifierProviderBCFips;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;

import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.SingleResp;

public class BasicOCSPRespBCFips implements IBasicOCSPResp {
    private final BasicOCSPResp basicOCSPRespBC;

    public BasicOCSPRespBCFips(BasicOCSPResp basicOCSPRespBC) {
        this.basicOCSPRespBC = basicOCSPRespBC;
    }

    public BasicOCSPResp getBasicOCSPRespBC() {
        return basicOCSPRespBC;
    }

    @Override
    public ISingleResp[] getResponses() {
        SingleResp[] resps = basicOCSPRespBC.getResponses();
        ISingleResp[] respsBC = new ISingleResp[resps.length];
        for (int i = 0; i < respsBC.length; i++) {
            respsBC[i] = new SingleRespBCFips(resps[i]);
        }
        return respsBC;
    }

    @Override
    public boolean isSignatureValid(IContentVerifierProvider provider) throws OCSPExceptionBCFips {
        try {
            return basicOCSPRespBC.isSignatureValid(((ContentVerifierProviderBCFips) provider).getProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }

    @Override
    public IX509CertificateHolder[] getCerts() {
        X509CertificateHolder[] certs = basicOCSPRespBC.getCerts();
        IX509CertificateHolder[] certsBC = new IX509CertificateHolder[certs.length];
        for (int i = 0; i < certs.length; i++) {
            certsBC[i] = new X509CertificateHolderBCFips(certs[i]);
        }
        return certsBC;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return basicOCSPRespBC.getEncoded();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicOCSPRespBCFips that = (BasicOCSPRespBCFips) o;
        return Objects.equals(basicOCSPRespBC, that.basicOCSPRespBC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicOCSPRespBC);
    }

    @Override
    public String toString() {
        return basicOCSPRespBC.toString();
    }
}
