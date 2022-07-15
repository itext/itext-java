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

/**
 * Wrapper class for {@link X509ExtensionUtils}.
 */
public class X509ExtensionUtilsBC implements IX509ExtensionUtils {
    private final X509ExtensionUtils extensionUtils;

    /**
     * Creates new wrapper instance for {@link X509ExtensionUtils}.
     *
     * @param extensionUtils {@link X509ExtensionUtils} to be wrapped
     */
    public X509ExtensionUtilsBC(X509ExtensionUtils extensionUtils) {
        this.extensionUtils = extensionUtils;
    }

    /**
     * Creates new wrapper instance for {@link X509ExtensionUtils}.
     *
     * @param digestCalculator DigestCalculator wrapper to create {@link X509ExtensionUtils}
     */
    public X509ExtensionUtilsBC(IDigestCalculator digestCalculator) {
        this(new X509ExtensionUtils(((DigestCalculatorBC) digestCalculator).getDigestCalculator()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link X509ExtensionUtils}.
     */
    public X509ExtensionUtils getExtensionUtils() {
        return extensionUtils;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAuthorityKeyIdentifier createAuthorityKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo) {
        return new AuthorityKeyIdentifierBC(extensionUtils.createAuthorityKeyIdentifier(
                ((SubjectPublicKeyInfoBC) publicKeyInfo).getSubjectPublicKeyInfo()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISubjectKeyIdentifier createSubjectKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo) {
        return new SubjectKeyIdentifierBC(extensionUtils.createSubjectKeyIdentifier(
                ((SubjectPublicKeyInfoBC) publicKeyInfo).getSubjectPublicKeyInfo()));
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
        X509ExtensionUtilsBC that = (X509ExtensionUtilsBC) o;
        return Objects.equals(extensionUtils, that.extensionUtils);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(extensionUtils);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return extensionUtils.toString();
    }
}
