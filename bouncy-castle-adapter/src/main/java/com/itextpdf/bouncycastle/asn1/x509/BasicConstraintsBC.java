package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IBasicConstraints;

import org.bouncycastle.asn1.x509.BasicConstraints;

/**
 * Wrapper class for {@link BasicConstraints}.
 */
public class BasicConstraintsBC extends ASN1EncodableBC implements IBasicConstraints {
    /**
     * Creates new wrapper instance for {@link BasicConstraints}.
     *
     * @param basicConstraints {@link BasicConstraints} to be wrapped
     */
    public BasicConstraintsBC(BasicConstraints basicConstraints) {
        super(basicConstraints);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link BasicConstraints}.
     */
    public BasicConstraints getBasicConstraints() {
        return (BasicConstraints) getEncodable();
    }
}
