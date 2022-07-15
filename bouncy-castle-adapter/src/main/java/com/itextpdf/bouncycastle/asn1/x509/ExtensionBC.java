package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import org.bouncycastle.asn1.x509.Extension;

/**
 * Wrapper class for {@link Extension}.
 */
public class ExtensionBC extends ASN1EncodableBC implements IExtension {
    private static final ExtensionBC INSTANCE = new ExtensionBC(null);

    private static final ASN1ObjectIdentifierBC C_RL_DISTRIBUTION_POINTS =
            new ASN1ObjectIdentifierBC(Extension.cRLDistributionPoints);

    private static final ASN1ObjectIdentifierBC AUTHORITY_INFO_ACCESS =
            new ASN1ObjectIdentifierBC(Extension.authorityInfoAccess);

    private static final ASN1ObjectIdentifierBC BASIC_CONSTRAINTS =
            new ASN1ObjectIdentifierBC(Extension.basicConstraints);

    private static final ASN1ObjectIdentifierBC KEY_USAGE =
            new ASN1ObjectIdentifierBC(Extension.keyUsage);

    private static final ASN1ObjectIdentifierBC EXTENDED_KEY_USAGE =
            new ASN1ObjectIdentifierBC(Extension.extendedKeyUsage);

    private static final ASN1ObjectIdentifierBC AUTHORITY_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBC(Extension.authorityKeyIdentifier);

    private static final ASN1ObjectIdentifierBC SUBJECT_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBC(Extension.subjectKeyIdentifier);

    /**
     * Creates new wrapper instance for {@link Extension}.
     *
     * @param extension {@link Extension} to be wrapped
     */
    public ExtensionBC(Extension extension) {
        super(extension);
    }

    /**
     * Creates new wrapper instance for {@link Extension}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param critical         boolean
     * @param octetString      ASN1OctetString wrapper
     */
    public ExtensionBC(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString) {
        super(new Extension(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                critical,
                ((ASN1OctetStringBC) octetString).getASN1OctetString()));
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link ExtensionBC} instance.
     */
    public static ExtensionBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link Extension}.
     */
    public Extension getExtension() {
        return (Extension) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getCRlDistributionPoints() {
        return C_RL_DISTRIBUTION_POINTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getAuthorityInfoAccess() {
        return AUTHORITY_INFO_ACCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getBasicConstraints() {
        return BASIC_CONSTRAINTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getKeyUsage() {
        return KEY_USAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getExtendedKeyUsage() {
        return EXTENDED_KEY_USAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getAuthorityKeyIdentifier() {
        return AUTHORITY_KEY_IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getSubjectKeyIdentifier() {
        return SUBJECT_KEY_IDENTIFIER;
    }
}
