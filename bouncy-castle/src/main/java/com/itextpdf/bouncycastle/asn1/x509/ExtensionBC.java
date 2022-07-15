package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import org.bouncycastle.asn1.x509.Extension;

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

    public ExtensionBC(Extension extension) {
        super(extension);
    }

    public ExtensionBC(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString) {
        super(new Extension(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                critical,
                ((ASN1OctetStringBC) octetString).getASN1OctetString()));
    }

    public static ExtensionBC getInstance() {
        return INSTANCE;
    }

    public Extension getExtension() {
        return (Extension) getEncodable();
    }

    @Override
    public IASN1ObjectIdentifier getCRlDistributionPoints() {
        return C_RL_DISTRIBUTION_POINTS;
    }

    @Override
    public IASN1ObjectIdentifier getAuthorityInfoAccess() {
        return AUTHORITY_INFO_ACCESS;
    }

    @Override
    public IASN1ObjectIdentifier getBasicConstraints() {
        return BASIC_CONSTRAINTS;
    }

    @Override
    public IASN1ObjectIdentifier getKeyUsage() {
        return KEY_USAGE;
    }

    @Override
    public IASN1ObjectIdentifier getExtendedKeyUsage() {
        return EXTENDED_KEY_USAGE;
    }

    @Override
    public IASN1ObjectIdentifier getAuthorityKeyIdentifier() {
        return AUTHORITY_KEY_IDENTIFIER;
    }

    @Override
    public IASN1ObjectIdentifier getSubjectKeyIdentifier() {
        return SUBJECT_KEY_IDENTIFIER;
    }
}
