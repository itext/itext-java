package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;

import org.bouncycastle.asn1.x509.Extensions;

/**
 * Wrapper class for {@link Extensions}.
 */
public class ExtensionsBC extends ASN1EncodableBC implements IExtensions {
    /**
     * Creates new wrapper instance for {@link Extensions}.
     *
     * @param extensions {@link Extensions} to be wrapped
     */
    public ExtensionsBC(Extensions extensions) {
        super(extensions);
    }

    /**
     * Creates new wrapper instance for {@link Extensions}.
     *
     * @param extensions Extension wrapper
     */
    public ExtensionsBC(IExtension extensions) {
        super(new Extensions(((ExtensionBC) extensions).getExtension()));
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
