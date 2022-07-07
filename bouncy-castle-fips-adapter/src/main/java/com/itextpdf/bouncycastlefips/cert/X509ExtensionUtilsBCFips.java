package com.itextpdf.bouncycastlefips.cert;

import com.itextpdf.bouncycastlefips.asn1.x509.AuthorityKeyIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.SubjectKeyIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.SubjectPublicKeyInfoBCFips;
import com.itextpdf.bouncycastlefips.operator.DigestCalculatorBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAuthorityKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.cert.IX509ExtensionUtils;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;

import java.util.Objects;
import org.bouncycastle.cert.X509ExtensionUtils;

public class X509ExtensionUtilsBCFips implements IX509ExtensionUtils {
    private final X509ExtensionUtils extensionUtils;

    public X509ExtensionUtilsBCFips(X509ExtensionUtils extensionUtils) {
        this.extensionUtils = extensionUtils;
    }

    public X509ExtensionUtilsBCFips(IDigestCalculator digestCalculator) {
        this(new X509ExtensionUtils(((DigestCalculatorBCFips) digestCalculator).getDigestCalculator()));
    }

    public X509ExtensionUtils getExtensionUtils() {
        return extensionUtils;
    }

    @Override
    public IAuthorityKeyIdentifier createAuthorityKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo) {
        return new AuthorityKeyIdentifierBCFips(extensionUtils.createAuthorityKeyIdentifier(
                ((SubjectPublicKeyInfoBCFips) publicKeyInfo).getSubjectPublicKeyInfo()));
    }

    @Override
    public ISubjectKeyIdentifier createSubjectKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo) {
        return new SubjectKeyIdentifierBCFips(extensionUtils.createSubjectKeyIdentifier(
                ((SubjectPublicKeyInfoBCFips) publicKeyInfo).getSubjectPublicKeyInfo()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        X509ExtensionUtilsBCFips that = (X509ExtensionUtilsBCFips) o;
        return Objects.equals(extensionUtils, that.extensionUtils);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensionUtils);
    }

    @Override
    public String toString() {
        return extensionUtils.toString();
    }
}
