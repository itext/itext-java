package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificateV2;

import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;

public class SigningCertificateV2BCFips extends ASN1EncodableBCFips implements ISigningCertificateV2 {
    public SigningCertificateV2BCFips(SigningCertificateV2 signingCertificateV2) {
        super(signingCertificateV2);
    }

    public SigningCertificateV2 getSigningCertificateV2() {
        return (SigningCertificateV2) getEncodable();
    }

    @Override
    public IESSCertIDv2[] getCerts() {
        ESSCertIDv2[] certs = getSigningCertificateV2().getCerts();
        IESSCertIDv2[] certsBCFips = new IESSCertIDv2[certs.length];
        for (int i = 0; i < certsBCFips.length; i++) {
            certsBCFips[i] = new ESSCertIDv2BCFips(certs[i]);
        }
        return certsBCFips;
    }
}
