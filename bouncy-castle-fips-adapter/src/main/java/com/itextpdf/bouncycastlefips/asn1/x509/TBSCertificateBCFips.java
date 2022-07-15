package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1IntegerBCFips;
import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;

import org.bouncycastle.asn1.x509.TBSCertificate;

/**
 * Wrapper class for {@link TBSCertificate}.
 */
public class TBSCertificateBCFips extends ASN1EncodableBCFips implements ITBSCertificate {
    /**
     * Creates new wrapper instance for {@link TBSCertificate}.
     *
     * @param tbsCertificate {@link TBSCertificate} to be wrapped
     */
    public TBSCertificateBCFips(TBSCertificate tbsCertificate) {
        super(tbsCertificate);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TBSCertificate}.
     */
    public TBSCertificate getTBSCertificate() {
        return (TBSCertificate) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return new SubjectPublicKeyInfoBCFips(getTBSCertificate().getSubjectPublicKeyInfo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX500Name getIssuer() {
        return new X500NameBCFips(getTBSCertificate().getIssuer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Integer getSerialNumber() {
        return new ASN1IntegerBCFips(getTBSCertificate().getSerialNumber());
    }
}
