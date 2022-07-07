package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;

import org.bouncycastle.asn1.x509.Extensions;

public class ExtensionsBC extends ASN1EncodableBC implements IExtensions {
    public ExtensionsBC(Extensions extensions) {
        super(extensions);
    }

    public ExtensionsBC(IExtension extensions) {
        super(new Extensions(((ExtensionBC) extensions).getExtension()));
    }

    public Extensions getExtensions() {
        return (Extensions) getEncodable();
    }
}
