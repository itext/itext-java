package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import org.bouncycastle.asn1.x509.Extension;

public class ExtensionBCFips extends ASN1EncodableBCFips implements IExtension {

    private static final ExtensionBCFips INSTANCE = new ExtensionBCFips(null);

    private static final ASN1ObjectIdentifierBCFips C_RL_DISTRIBUTION_POINTS =
            new ASN1ObjectIdentifierBCFips(Extension.cRLDistributionPoints);

    private static final ASN1ObjectIdentifierBCFips AUTHORITY_INFO_ACCESS =
            new ASN1ObjectIdentifierBCFips(Extension.authorityInfoAccess);

    public ExtensionBCFips(Extension extension) {
        super(extension);
    }

    public ExtensionBCFips(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString) {
        super(new Extension(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                critical,
                ((ASN1OctetStringBCFips) octetString).getOctetString()));
    }

    public static ExtensionBCFips getInstance() {
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
