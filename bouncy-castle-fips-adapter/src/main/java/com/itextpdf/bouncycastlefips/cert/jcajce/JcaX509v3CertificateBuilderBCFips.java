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

/**
 * Wrapper class for {@link JcaX509v3CertificateBuilder}.
 */
public class JcaX509v3CertificateBuilderBCFips implements IJcaX509v3CertificateBuilder {
    private final JcaX509v3CertificateBuilder certificateBuilder;

    /**
     * Creates new wrapper instance for {@link JcaX509v3CertificateBuilder}.
     *
     * @param certificateBuilder {@link JcaX509v3CertificateBuilder} to be wrapped
     */
    public JcaX509v3CertificateBuilderBCFips(JcaX509v3CertificateBuilder certificateBuilder) {
        this.certificateBuilder = certificateBuilder;
    }

    /**
     * Creates new wrapper instance for {@link JcaX509v3CertificateBuilder}.
     *
     * @param signingCert      X509Certificate to create {@link JcaX509v3CertificateBuilder}
     * @param certSerialNumber BigInteger to create {@link JcaX509v3CertificateBuilder}
     * @param startDate        start date to create {@link JcaX509v3CertificateBuilder}
     * @param endDate          end date to create {@link JcaX509v3CertificateBuilder}
     * @param subjectDnName    X500Name wrapper to create {@link JcaX509v3CertificateBuilder}
     * @param publicKey        PublicKey to create {@link JcaX509v3CertificateBuilder}
     */
    public JcaX509v3CertificateBuilderBCFips(X509Certificate signingCert, BigInteger certSerialNumber, Date startDate,
            Date endDate, IX500Name subjectDnName, PublicKey publicKey) {
        this(new JcaX509v3CertificateBuilder(signingCert, certSerialNumber, startDate, endDate,
                ((X500NameBCFips) subjectDnName).getX500Name(), publicKey));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaX509v3CertificateBuilder}.
     */
    public JcaX509v3CertificateBuilder getCertificateBuilder() {
        return certificateBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX509CertificateHolder build(IContentSigner contentSigner) {
        return new X509CertificateHolderBCFips(certificateBuilder.build(
                ((ContentSignerBCFips) contentSigner).getContentSigner()));
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
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

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateBuilder.toString();
    }
}
