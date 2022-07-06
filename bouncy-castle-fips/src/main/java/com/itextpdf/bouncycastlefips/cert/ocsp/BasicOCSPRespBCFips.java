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
    private final BasicOCSPResp basicOCSPResp;

    public BasicOCSPRespBCFips(BasicOCSPResp basicOCSPResp) {
        this.basicOCSPResp = basicOCSPResp;
    }

    public BasicOCSPResp getBasicOCSPResp() {
        return basicOCSPResp;
    }

    @Override
    public ISingleResp[] getResponses() {
        SingleResp[] resps = basicOCSPResp.getResponses();
        ISingleResp[] respsBCFips = new ISingleResp[resps.length];
        for (int i = 0; i < respsBCFips.length; i++) {
            respsBCFips[i] = new SingleRespBCFips(resps[i]);
        }
        return respsBCFips;
    }

    @Override
    public boolean isSignatureValid(IContentVerifierProvider provider) throws OCSPExceptionBCFips {
        try {
            return basicOCSPResp.isSignatureValid(((ContentVerifierProviderBCFips) provider).getProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }

    @Override
    public IX509CertificateHolder[] getCerts() {
        X509CertificateHolder[] certs = basicOCSPResp.getCerts();
        IX509CertificateHolder[] certsBCFips = new IX509CertificateHolder[certs.length];
        for (int i = 0; i < certs.length; i++) {
            certsBCFips[i] = new X509CertificateHolderBCFips(certs[i]);
        }
        return certsBCFips;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return basicOCSPResp.getEncoded();
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
