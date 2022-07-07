package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1IntegerBC;
import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;

import org.bouncycastle.asn1.x509.TBSCertificate;

public class TBSCertificateBC extends ASN1EncodableBC implements ITBSCertificate {
    public TBSCertificateBC(TBSCertificate tbsCertificate) {
        super(tbsCertificate);
    }

    public TBSCertificate getTBSCertificate() {
        return (TBSCertificate) getEncodable();
    }

    @Override
    public ISubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return new SubjectPublicKeyInfoBC(getTBSCertificate().getSubjectPublicKeyInfo());
    }

    @Override
    public IX500Name getIssuer() {
        return new X500NameBC(getTBSCertificate().getIssuer());
    }

    @Override
    public IASN1Integer getSerialNumber() {
        return new ASN1IntegerBC(getTBSCertificate().getSerialNumber());
    }
}
