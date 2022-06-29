package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import org.bouncycastle.asn1.x509.Extension;

public class ExtensionBCFips extends ASN1EncodableBCFips implements IExtension {
    public ExtensionBCFips(Extension extension) {
        super(extension);
    }

    public ExtensionBCFips(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString) {
        super(new Extension(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getObjectIdentifier(),
                critical,
                ((ASN1OctetStringBCFips) octetString).getOctetString()));
    }

    public Extension getExtension() {
        return (Extension) getEncodable();
    }
}
