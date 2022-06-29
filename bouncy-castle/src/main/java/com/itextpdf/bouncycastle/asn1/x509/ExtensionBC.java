package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import org.bouncycastle.asn1.x509.Extension;

public class ExtensionBC extends ASN1EncodableBC implements IExtension {
    public ExtensionBC(Extension extension) {
        super(extension);
    }
    
    public ExtensionBC(IASN1ObjectIdentifier objectIdentifier, boolean critical, IASN1OctetString octetString) {
        super(new Extension(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getObjectIdentifier(),
                critical,
                ((ASN1OctetStringBC) octetString).getOctetString()));
    }
    
    public Extension getExtension() {
        return (Extension) getEncodable();
    }
}
