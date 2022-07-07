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

    public IASN1ObjectIdentifier getCRlDistributionPoints() {
        return C_RL_DISTRIBUTION_POINTS;
    }

    public IASN1ObjectIdentifier getAuthorityInfoAccess() {
        return AUTHORITY_INFO_ACCESS;
    }

    public Extension getExtension() {
        return (Extension) getEncodable();
    }
}
