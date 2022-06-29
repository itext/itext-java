package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.operator.ContentVerifierProviderBC;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;

import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.SingleResp;

import java.util.Arrays;
import java.util.stream.Stream;

public class BasicOCSPRespBC implements IBasicOCSPResp {
    private final BasicOCSPResp basicOCSPRespBC;

    public BasicOCSPRespBC(BasicOCSPResp basicOCSPRespBC) {
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
            respsBC[i] = new SingleRespBC(resps[i]);
        }
        return respsBC;
    }
    
    @Override
    public boolean isSignatureValid(IContentVerifierProvider provider) throws OCSPExceptionBC {
        try {
            return basicOCSPRespBC.isSignatureValid(((ContentVerifierProviderBC) provider).getProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }
    
    @Override
    public IX509CertificateHolder[] getCerts() {
        X509CertificateHolder[] certs = basicOCSPRespBC.getCerts();
        IX509CertificateHolder[] certsBC = new IX509CertificateHolder[certs.length];
        for (int i = 0; i < certs.length; i++) {
            certsBC[i] = new X509CertificateHolderBC(certs[i]);
        }
        return certsBC;
    }
}
