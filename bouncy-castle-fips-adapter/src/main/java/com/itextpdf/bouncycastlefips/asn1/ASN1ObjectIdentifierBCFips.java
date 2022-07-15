package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * Wrapper class for {@link ASN1ObjectIdentifier}.
 */
public class ASN1ObjectIdentifierBCFips extends ASN1PrimitiveBCFips implements IASN1ObjectIdentifier {
    /**
     * Creates new wrapper instance for {@link ASN1ObjectIdentifier}.
     *
     * @param identifier string to create {@link ASN1ObjectIdentifier}
     */
    public ASN1ObjectIdentifierBCFips(String identifier) {
        super(new ASN1ObjectIdentifier(identifier));
    }

    /**
     * Creates new wrapper instance for {@link ASN1ObjectIdentifier}.
     *
     * @param identifier {@link ASN1ObjectIdentifier} to be wrapped
     */
    public ASN1ObjectIdentifierBCFips(ASN1ObjectIdentifier identifier) {
        super(identifier);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1ObjectIdentifier}.
     */
    public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
        return (ASN1ObjectIdentifier) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return getASN1ObjectIdentifier().getId();
    }
}
