package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificateV2;

import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;

public class SigningCertificateV2BCFips implements ISigningCertificateV2 {
    private final SigningCertificateV2 signingCertificateV2;

    public SigningCertificateV2BCFips(SigningCertificateV2 signingCertificateV2) {
        this.signingCertificateV2 = signingCertificateV2;
    }

    public SigningCertificateV2 getSigningCertificateV2() {
        return signingCertificateV2;
    }

    @Override
    public IESSCertIDv2[] getCerts() {
        ESSCertIDv2[] certs = signingCertificateV2.getCerts();
        IESSCertIDv2[] certsBC = new IESSCertIDv2[certs.length];
        for (int i = 0; i < certsBC.length; i++) {
            certsBC[i] = new ESSCertIDv2BCFips(certs[i]);
        }
        return certsBC;
    }
}
