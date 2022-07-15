package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;

/**
 * Wrapper class for {@link ASN1Primitive}.
 */
public class ASN1PrimitiveBCFips extends ASN1EncodableBCFips implements IASN1Primitive {
    /**
     * Creates new wrapper instance for {@link ASN1Primitive}.
     *
     * @param primitive {@link ASN1Primitive} to be wrapped
     */
    public ASN1PrimitiveBCFips(ASN1Primitive primitive) {
        super(primitive);
    }

    /**
     * Creates new wrapper instance for {@link ASN1Primitive}.
     *
     * @param array byte array to create {@link ASN1Primitive} to be wrapped
     */
    public ASN1PrimitiveBCFips(byte[] array) throws IOException {
        super(ASN1Primitive.fromByteArray(array));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Primitive}.
     */
    public ASN1Primitive getPrimitive() {
        return (ASN1Primitive) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return getPrimitive().getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded(String encoding) throws IOException {
        return getPrimitive().getEncoded(encoding);
    }
}
