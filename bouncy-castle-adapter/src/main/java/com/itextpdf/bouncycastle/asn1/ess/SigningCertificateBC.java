package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;

import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;

public class SigningCertificateBC extends ASN1EncodableBC implements ISigningCertificate {
    public SigningCertificateBC(SigningCertificate signingCertificate) {
        super(signingCertificate);
    }

    public SigningCertificate getSigningCertificate() {
        return (SigningCertificate) getEncodable();
    }

    @Override
    public IESSCertID[] getCerts() {
        ESSCertID[] certs = getSigningCertificate().getCerts();
        IESSCertID[] certsBC = new IESSCertID[certs.length];
        for (int i = 0; i < certsBC.length; i++) {
            certsBC[i] = new ESSCertIDBC(certs[i]);
        }
        return certsBC;
    }
}
