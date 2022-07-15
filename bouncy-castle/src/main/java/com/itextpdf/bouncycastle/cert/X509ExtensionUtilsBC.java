package com.itextpdf.bouncycastle.cert;

import com.itextpdf.bouncycastle.asn1.x509.AuthorityKeyIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.SubjectKeyIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.SubjectPublicKeyInfoBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAuthorityKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.cert.IX509ExtensionUtils;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;

import java.util.Objects;
import org.bouncycastle.cert.X509ExtensionUtils;

public class X509ExtensionUtilsBC implements IX509ExtensionUtils {
    private final X509ExtensionUtils extensionUtils;

    public X509ExtensionUtilsBC(X509ExtensionUtils extensionUtils) {
        this.extensionUtils = extensionUtils;
    }

    public X509ExtensionUtilsBC(IDigestCalculator digestCalculator) {
        this(new X509ExtensionUtils(((DigestCalculatorBC) digestCalculator).getDigestCalculator()));
    }

    public X509ExtensionUtils getExtensionUtils() {
        return extensionUtils;
    }

    @Override
    public IAuthorityKeyIdentifier createAuthorityKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo) {
        return new AuthorityKeyIdentifierBC(extensionUtils.createAuthorityKeyIdentifier(
                ((SubjectPublicKeyInfoBC) publicKeyInfo).getSubjectPublicKeyInfo()));
    }

    @Override
    public ISubjectKeyIdentifier createSubjectKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo) {
        return new SubjectKeyIdentifierBC(extensionUtils.createSubjectKeyIdentifier(
                ((SubjectPublicKeyInfoBC) publicKeyInfo).getSubjectPublicKeyInfo()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        X509ExtensionUtilsBC that = (X509ExtensionUtilsBC) o;
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
