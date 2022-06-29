package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import org.bouncycastle.asn1.x509.Extensions;

public class ExtensionsBCFips extends ASN1EncodableBCFips implements IExtensions {
    public ExtensionsBCFips(Extensions extensions) {
        super(extensions);
    }

    public ExtensionsBCFips(IExtension extensions) {
        super(new Extensions(((ExtensionBCFips) extensions).getExtension()));
    }

    public Extensions getExtensions() {
        return (Extensions) getEncodable();
    }
}
