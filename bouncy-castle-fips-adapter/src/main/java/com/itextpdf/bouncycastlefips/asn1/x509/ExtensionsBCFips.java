package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;

import org.bouncycastle.asn1.x509.Extensions;

/**
 * Wrapper class for {@link Extensions}.
 */
public class ExtensionsBCFips extends ASN1EncodableBCFips implements IExtensions {
    /**
     * Creates new wrapper instance for {@link Extensions}.
     *
     * @param extensions {@link Extensions} to be wrapped
     */
    public ExtensionsBCFips(Extensions extensions) {
        super(extensions);
    }

    /**
     * Creates new wrapper instance for {@link Extensions}.
     *
     * @param extensions Extension wrapper
     */
    public ExtensionsBCFips(IExtension extensions) {
        super(new Extensions(((ExtensionBCFips) extensions).getExtension()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link Extensions}.
     */
    public Extensions getExtensions() {
        return (Extensions) getEncodable();
    }
}
