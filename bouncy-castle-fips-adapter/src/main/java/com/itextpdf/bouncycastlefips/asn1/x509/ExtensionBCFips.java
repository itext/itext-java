package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import org.bouncycastle.asn1.x509.Extension;

/**
 * Wrapper class for {@link Extension}.
 */
public class ExtensionBCFips extends ASN1EncodableBCFips implements IExtension {
    private static final ExtensionBCFips INSTANCE = new ExtensionBCFips(null);

    private static final ASN1ObjectIdentifierBCFips C_RL_DISTRIBUTION_POINTS =
            new ASN1ObjectIdentifierBCFips(Extension.cRLDistributionPoints);

    private static final ASN1ObjectIdentifierBCFips AUTHORITY_INFO_ACCESS =
            new ASN1ObjectIdentifierBCFips(Extension.authorityInfoAccess);

    private static final ASN1ObjectIdentifierBCFips BASIC_CONSTRAINTS =
            new ASN1ObjectIdentifierBCFips(Extension.basicConstraints);

    private static final ASN1ObjectIdentifierBCFips KEY_USAGE =
            new ASN1ObjectIdentifierBCFips(Extension.keyUsage);

    private static final ASN1ObjectIdentifierBCFips EXTENDED_KEY_USAGE =
            new ASN1ObjectIdentifierBCFips(Extension.extendedKeyUsage);

    private static final ASN1ObjectIdentifierBCFips AUTHORITY_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBCFips(Extension.authorityKeyIdentifier);

    private static final ASN1ObjectIdentifierBCFips SUBJECT_KEY_IDENTIFIER =
            new ASN1ObjectIdentifierBCFips(Extension.subjectKeyIdentifier);

    /**
     * Creates new wrapper instance for {@link Extension}.
     *
     * @param extension {@link Extension} to be wrapped
     */
    public ExtensionBCFips(Extension extension) {
        super(extension);
    }

    /**
     * Creates new wrapper instance for {@link Extension}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param critical         boolean
     * @param octetString      ASN1OctetString wrapper
     */
    public ExtensionBCFips(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString) {
        super(new Extension(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                critical,
                ((ASN1OctetStringBCFips) octetString).getOctetString()));
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link ExtensionBCFips} instance.
     */
    public static ExtensionBCFips getInstance() {
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
