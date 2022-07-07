package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.bouncycastlefips.cert.CertIOExceptionBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.operator.ContentSignerBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509v3CertificateBuilder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;

public class JcaX509v3CertificateBuilderBCFips implements IJcaX509v3CertificateBuilder {
    private final JcaX509v3CertificateBuilder certificateBuilder;

    public JcaX509v3CertificateBuilderBCFips(JcaX509v3CertificateBuilder certificateBuilder) {
        this.certificateBuilder = certificateBuilder;
    }

    public JcaX509v3CertificateBuilderBCFips(X509Certificate signingCert, BigInteger certSerialNumber, Date startDate,
            Date endDate, IX500Name subjectDnName, PublicKey publicKey) {
        this(new JcaX509v3CertificateBuilder(signingCert, certSerialNumber, startDate, endDate,
                ((X500NameBCFips) subjectDnName).getX500Name(), publicKey));
    }

    public JcaX509v3CertificateBuilder getCertificateBuilder() {
        return certificateBuilder;
    }

    @Override
    public IX509CertificateHolder build(IContentSigner contentSigner) {
        return new X509CertificateHolderBCFips(certificateBuilder.build(
                ((ContentSignerBCFips) contentSigner).getContentSigner()));
    }

    @Override
    public IJcaX509v3CertificateBuilder addExtension(IASN1ObjectIdentifier extensionOID, boolean critical,
            IASN1Encodable extensionValue) throws CertIOExceptionBCFips {
        try {
            certificateBuilder.addExtension(((ASN1ObjectIdentifierBCFips) extensionOID).getASN1ObjectIdentifier(),
                    critical, ((ASN1EncodableBCFips) extensionValue).getEncodable());
            return this;
        } catch (CertIOException e) {
            throw new CertIOExceptionBCFips(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaX509v3CertificateBuilderBCFips that = (JcaX509v3CertificateBuilderBCFips) o;
        return Objects.equals(certificateBuilder, that.certificateBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateBuilder);
    }

    @Override
    public String toString() {
        return certificateBuilder.toString();
    }
}
