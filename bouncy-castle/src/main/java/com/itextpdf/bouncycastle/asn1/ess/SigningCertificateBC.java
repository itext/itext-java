package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;

import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;

public class SigningCertificateBC implements ISigningCertificate {
    private final SigningCertificate signingCertificate;

    public SigningCertificateBC(SigningCertificate signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public SigningCertificate getSigningCertificate() {
        return signingCertificate;
    }

    @Override
    public IESSCertID[] getCerts() {
        ESSCertID[] certs = signingCertificate.getCerts();
        IESSCertID[] certsBC = new IESSCertID[certs.length];
        for (int i = 0; i < certsBC.length; i++) {
            certsBC[i] = new ESSCertIDBC(certs[i]);
        }
        return certsBC;
    }
}
