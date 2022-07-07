package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;

import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;

public class SigningCertificateBCFips extends ASN1EncodableBCFips implements ISigningCertificate {
    public SigningCertificateBCFips(SigningCertificate signingCertificate) {
        super(signingCertificate);
    }

    public SigningCertificate getSigningCertificate() {
        return (SigningCertificate) getEncodable();
    }

    @Override
    public IESSCertID[] getCerts() {
        ESSCertID[] certs = getSigningCertificate().getCerts();
        IESSCertID[] certsBCFips = new IESSCertID[certs.length];
        for (int i = 0; i < certsBCFips.length; i++) {
            certsBCFips[i] = new ESSCertIDBCFips(certs[i]);
        }
        return certsBCFips;
    }
}
