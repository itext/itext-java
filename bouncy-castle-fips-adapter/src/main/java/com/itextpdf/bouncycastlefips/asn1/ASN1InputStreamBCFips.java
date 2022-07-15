package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1InputStream;

/**
 * Wrapper class for {@link ASN1InputStream}.
 */
public class ASN1InputStreamBCFips implements IASN1InputStream {
    private final ASN1InputStream stream;

    /**
     * Creates new wrapper instance for {@link ASN1InputStream}.
     *
     * @param asn1InputStream {@link ASN1InputStream} to be wrapped
     */
    public ASN1InputStreamBCFips(ASN1InputStream asn1InputStream) {
        this.stream = asn1InputStream;
    }

    /**
     * Creates new wrapper instance for {@link ASN1InputStream}.
     *
     * @param bytes byte array to create {@link ASN1InputStream}
     */
    public ASN1InputStreamBCFips(byte[] bytes) {
        this.stream = new ASN1InputStream(bytes);
    }

    /**
     * Creates new wrapper instance for {@link ASN1InputStream}.
     *
     * @param stream InputStream to create {@link ASN1InputStream}
     */
    public ASN1InputStreamBCFips(InputStream stream) {
        this.stream = new ASN1InputStream(stream);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1InputStream}.
     */
    public ASN1InputStream getASN1InputStream() {
        return stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive readObject() throws IOException {
        return new ASN1PrimitiveBCFips(stream.readObject());
    }

    /**
     * Delegates {@code close} method call to the wrapped stream.
     */
    @Override
    public void close() throws IOException {
        stream.close();
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1InputStreamBCFips that = (ASN1InputStreamBCFips) o;
        return Objects.equals(stream, that.stream);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(stream);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return stream.toString();
    }
}
