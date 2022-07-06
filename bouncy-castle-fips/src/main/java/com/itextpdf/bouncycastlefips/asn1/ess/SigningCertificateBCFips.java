package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;

import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;

public class SigningCertificateBCFips implements ISigningCertificate {
    private final SigningCertificate signingCertificate;

    public SigningCertificateBCFips(SigningCertificate signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public SigningCertificate getSigningCertificate() {
        return signingCertificate;
    }

    @Override
    public IESSCertID[] getCerts() {
        ESSCertID[] certs = signingCertificate.getCerts();
        IESSCertID[] certsBCFips = new IESSCertID[certs.length];
        for (int i = 0; i < certsBCFips.length; i++) {
            certsBCFips[i] = new ESSCertIDBCFips(certs[i]);
        }
        return certsBCFips;
    }
}
